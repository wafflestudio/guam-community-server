package waffle.guam.favorite.service.command

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.entity.LikeEntity
import waffle.guam.favorite.data.r2dbc.repository.LikeRepository
import waffle.guam.favorite.data.redis.repository.PostLikeCountRepository
import waffle.guam.favorite.service.CommandHandler
import waffle.guam.favorite.service.Event
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.FavoriteKafkaProducer
import waffle.guam.favorite.service.model.Like
import java.time.Instant

@Service
class LikeCreateHandler(
    private val likeRepository: LikeRepository,
    private val likeCountRepository: PostLikeCountRepository,
    private val community: CommunityService,
    private val kafka: FavoriteKafkaProducer
) : CommandHandler<Like, LikeCreated> {

    @Transactional
    override suspend fun handle(command: Like): LikeCreated = coroutineScope {
        val postId = command.postId
        val userId = command.userId

        val post = async { community.getPost(command.postId).let(::requireNotNull) }

        if (likeRepository.existsByPostIdAndUserId(postId = postId, userId = userId)) {
            throw DuplicateLikeException()
        }

        likeRepository.save(LikeEntity(postId = postId, userId = userId))

        likeCountRepository.increment(boardId = post.await().boardId, postId = postId)

        LikeCreated(postId = postId, userId = userId).also(kafka::send)
    }
}

@Service
class LikeDeleteHandler(
    private val likeRepository: LikeRepository,
    private val likeCountRepository: PostLikeCountRepository,
    private val community: CommunityService,

) : CommandHandler<Like, LikeDeleted> {

    @Transactional
    override suspend fun handle(command: Like): LikeDeleted = coroutineScope {
        val postId = command.postId
        val userId = command.userId

        val post = async { community.getPost(command.postId).let(::requireNotNull) }

        val updatedRows = likeRepository.deleteByPostIdAndUserId(postId = postId, userId = userId)

        if (updatedRows < 1) {
            throw LikeNotFoundException()
        }

        likeCountRepository.decrement(boardId = post.await().boardId, postId = postId)

        LikeDeleted(postId = postId, userId = userId)
    }
}

data class LikeCreated(val postId: Long, val userId: Long, override val eventTime: Instant = Instant.now()) : Event
data class LikeDeleted(val postId: Long, val userId: Long, override val eventTime: Instant = Instant.now()) : Event

class DuplicateLikeException(
    override val status: Int = 409,
    override val msg: String = "이미 좋아요를 누른 게시물입니다.",
) : ServiceError()

class LikeNotFoundException(
    override val status: Int = 404,
    override val msg: String = "좋아요를 누르지 않은 게시물입니다.",
) : ServiceError()
