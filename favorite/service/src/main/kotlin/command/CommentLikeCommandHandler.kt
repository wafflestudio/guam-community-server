package waffle.guam.favorite.service.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.entity.CommentLikeEntity
import waffle.guam.favorite.data.r2dbc.repository.CommentLikeRepository
import waffle.guam.favorite.data.redis.repository.CommentLikeCountRepository
import waffle.guam.favorite.service.CommandHandler
import waffle.guam.favorite.service.Event
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.infra.FavoriteKafkaProducer
import waffle.guam.favorite.service.model.CommentLike
import java.time.Instant

@Service
class CommentLikeCreateHandler(
    private val commentLikeRepository: CommentLikeRepository,
    private val commentLikeCountRepository: CommentLikeCountRepository,
    private val kafka: FavoriteKafkaProducer
) : CommandHandler<CommentLike, CommentLikeCreated> {

    @Transactional
    override suspend fun handle(command: CommentLike): CommentLikeCreated {
        val postCommentId = command.postCommentId
        val userId = command.userId

        if (commentLikeRepository.existsByPostCommentIdAndUserId(postCommentId = postCommentId, userId = userId)) {
            throw DuplicateCommentLikeException()
        }

        commentLikeRepository.save(CommentLikeEntity(postCommentId = postCommentId, userId = userId))

        commentLikeCountRepository.increment(postCommentId)

        return CommentLikeCreated(postCommentId = postCommentId, userId = userId).also(kafka::send)
    }
}

@Service
class CommentLikeDeleteHandler(
    private val commentLikeRepository: CommentLikeRepository,
    private val commentLikeCountRepository: CommentLikeCountRepository,
) : CommandHandler<CommentLike, CommentLikeDeleted> {

    @Transactional
    override suspend fun handle(command: CommentLike): CommentLikeDeleted {
        val postCommentId = command.postCommentId
        val userId = command.userId

        val updatedRows = commentLikeRepository.deleteByPostCommentIdAndUserId(
            postCommentId = postCommentId,
            userId = userId
        )

        if (updatedRows < 1) {
            throw CommentLikeNotFoundException()
        }

        commentLikeCountRepository.decrement(postCommentId)

        return CommentLikeDeleted(postCommentId = postCommentId, userId = userId)
    }
}

data class CommentLikeCreated(
    val postCommentId: Long,
    val userId: Long,
    override val eventTime: Instant = Instant.now(),
) : Event

data class CommentLikeDeleted(
    val postCommentId: Long,
    val userId: Long,
    override val eventTime: Instant = Instant.now(),
) : Event

class DuplicateCommentLikeException(
    override val status: Int = 409,
    override val msg: String = "이미 좋아요를 누른 댓글입니다.",
) : ServiceError()

class CommentLikeNotFoundException(
    override val status: Int = 404,
    override val msg: String = "좋아요를 누르지 않은 댓글입니다.",
) : ServiceError()
