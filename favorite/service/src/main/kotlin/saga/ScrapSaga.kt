package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.ScrapCreated
import waffle.guam.favorite.service.command.ScrapDeleted
import waffle.guam.favorite.service.command.ScrapEvent
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.NotificationService
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest.Info
import waffle.guam.favorite.service.infra.Post
import waffle.guam.favorite.service.model.Scrap
import waffle.guam.favorite.service.query.ScrapCountStore

interface ScrapSaga {
    suspend fun handleEvent(event: ScrapEvent)
}

@Service
class ScrapSagaImpl(
    private val scrapCountStore: ScrapCountStore.Mutable,
    private val notification: NotificationService,
    private val community: CommunityService,
) : ScrapSaga {
    override suspend fun handleEvent(event: ScrapEvent) {
        val post = community.getPost(event.scrap.postId) ?: throw RuntimeException("POST NOT FOUND")

        when (event) {
            is ScrapCreated -> {
                // push
                notification.notify(CreateNotificationRequest(scrap = event.scrap, post = post))
                // increment scrap
                scrapCountStore.increment(event.scrap.postId)
            }
            is ScrapDeleted -> {
                scrapCountStore.decrement(event.scrap.postId)
            }
        }
    }
}

private fun CreateNotificationRequest(scrap: Scrap, post: Post): CreateNotificationRequest =
    CreateNotificationRequest(
        producerId = scrap.userId,
        infos = listOf(
            Info(
                consumerId = post.userId,
                kind = "POST_SCRAP",
                body = post.title,
                linkUrl = "/api/v1/posts/${post.id}",
                isAnonymousEvent = post.isAnonymous
            )
        )
    )
