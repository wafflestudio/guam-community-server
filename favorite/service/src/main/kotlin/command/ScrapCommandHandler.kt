package waffle.guam.favorite.service.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.ScrapEntity
import waffle.guam.favorite.data.r2dbc.ScrapRepository
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.model.Scrap
import waffle.guam.favorite.service.saga.ScrapSaga
import java.time.Instant

@Service
class ScrapCreateHandler(
    override val scrapRepository: ScrapRepository,
    override val scrapSaga: ScrapSaga,
) : ScrapCommandHandler() {
    override suspend fun internalHandle(scrap: Scrap): ScrapCreated {
        if (scrap.exists()) {
            throw DuplicateScrapException()
        }

        scrapRepository.save(ScrapEntity(postId = scrap.postId, userId = scrap.userId))

        return ScrapCreated(scrap)
    }
}

@Service
class ScrapDeleteHandler(
    override val scrapRepository: ScrapRepository,
    override val scrapSaga: ScrapSaga,
) : ScrapCommandHandler() {

    override suspend fun internalHandle(scrap: Scrap): ScrapDeleted {
        val updatedRows = scrapRepository.deleteByPostIdAndUserId(postId = scrap.postId, userId = scrap.userId)

        if (updatedRows < 1) {
            throw ScrapNotFoundException()
        }

        return ScrapDeleted(scrap)
    }
}

abstract class ScrapCommandHandler : CommandHandler<Scrap, ScrapEvent> {
    abstract val scrapRepository: ScrapRepository
    abstract val scrapSaga: ScrapSaga

    @Transactional
    override suspend fun handle(command: Scrap): ScrapEvent {
        // TODO: 더 느슨하게 불가능..?
        return internalHandle(command).also { scrapSaga.handleEvent(it) }
    }

    protected suspend fun Scrap.exists(): Boolean {
        return scrapRepository.existsByPostIdAndUserId(postId = postId, userId = userId)
    }

    protected abstract suspend fun internalHandle(scrap: Scrap): ScrapEvent
}

sealed class ScrapEvent(val eventTime: Instant = Instant.now())
data class ScrapCreated(val scrap: Scrap) : ScrapEvent()
data class ScrapDeleted(val scrap: Scrap) : ScrapEvent()

class DuplicateScrapException(
    override val status: Int = 409,
    override val msg: String = "이미 스크랩한 게시물입니다.",
) : ServiceError()

class ScrapNotFoundException(
    override val status: Int = 404,
    override val msg: String = "스크랩하지 않은 게시물입니다.",
) : ServiceError()
