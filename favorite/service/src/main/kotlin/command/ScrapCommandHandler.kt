package waffle.guam.favorite.service.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.entity.ScrapEntity
import waffle.guam.favorite.data.r2dbc.repository.ScrapRepository
import waffle.guam.favorite.data.redis.repository.PostScrapCountRepository
import waffle.guam.favorite.service.CommandHandler
import waffle.guam.favorite.service.Event
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.infra.FavoriteKafkaProducer
import waffle.guam.favorite.service.model.Scrap
import java.time.Instant

@Service
class ScrapCreateHandler(
    private val scrapRepository: ScrapRepository,
    private val scrapCountRepository: PostScrapCountRepository,
    private val kafka: FavoriteKafkaProducer
) : CommandHandler<Scrap, ScrapCreated> {

    @Transactional
    override suspend fun handle(command: Scrap): ScrapCreated {
        val postId = command.postId
        val userId = command.userId

        if (scrapRepository.existsByPostIdAndUserId(postId = postId, userId = userId)) {
            throw DuplicateScrapException()
        }

        scrapRepository.save(ScrapEntity(postId = postId, userId = userId))

        scrapCountRepository.increment(postId)

        return ScrapCreated(postId = postId, userId = userId).also(kafka::send)
    }
}

@Service
class ScrapDeleteHandler(
    private val scrapRepository: ScrapRepository,
    private val scrapCountRepository: PostScrapCountRepository,
) : CommandHandler<Scrap, ScrapDeleted> {

    @Transactional
    override suspend fun handle(command: Scrap): ScrapDeleted {
        val postId = command.postId
        val userId = command.userId

        val updatedRows = scrapRepository.deleteByPostIdAndUserId(
            postId = postId,
            userId = userId
        )

        if (updatedRows < 1) {
            throw ScrapNotFoundException()
        }

        scrapCountRepository.decrement(postId)

        return ScrapDeleted(postId = postId, userId = userId)
    }
}

data class ScrapCreated(
    val postId: Long,
    val userId: Long,
    override val eventTime: Instant = Instant.now(),
) : Event

data class ScrapDeleted(
    val postId: Long,
    val userId: Long,
    override val eventTime: Instant = Instant.now(),
) : Event

class DuplicateScrapException(
    override val status: Int = 409,
    override val msg: String = "이미 스크랩한 게시물입니다.",
) : ServiceError()

class ScrapNotFoundException(
    override val status: Int = 404,
    override val msg: String = "스크랩하지 않은 게시물입니다.",
) : ServiceError()
