package waffle.guam.favorite.service.command

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.ScrapEntity
import waffle.guam.favorite.data.r2dbc.ScrapRepository
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.Post
import waffle.guam.favorite.service.model.Scrap
import waffle.guam.favorite.service.saga.ScrapSaga
import java.time.Instant

@Service
class ScrapCreateHandler(
    override val scrapRepository: ScrapRepository,
    override val scrapSaga: ScrapSaga,
    private val community: CommunityService,
) : ScrapCommandHandler() {
    override suspend fun internalHandle(scrap: Scrap): ScrapCreated = coroutineScope {
        val post = async {
            community.getPost(scrap.postId) ?: throw RuntimeException("Valid Post Not Found")
        }

        if (scrap.exists()) {
            throw DuplicateScrapException()
        }

        scrapRepository.save(ScrapEntity(postId = scrap.postId, userId = scrap.userId))

        ScrapCreated(scrap = scrap, post = post.await())
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

sealed class ScrapEvent(override val eventTime: Instant = Instant.now()) : Event {
    abstract val scrap: Scrap
}

data class ScrapCreated(override val scrap: Scrap, val post: Post) : ScrapEvent()
data class ScrapDeleted(override val scrap: Scrap) : ScrapEvent()

class DuplicateScrapException(
    override val status: Int = 409,
    override val msg: String = "이미 스크랩한 게시물입니다.",
) : ServiceError()

class ScrapNotFoundException(
    override val status: Int = 404,
    override val msg: String = "스크랩하지 않은 게시물입니다.",
) : ServiceError()
