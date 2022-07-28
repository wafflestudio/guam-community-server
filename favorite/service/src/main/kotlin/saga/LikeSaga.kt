package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.LikeCreated
import waffle.guam.favorite.service.command.LikeDeleted
import waffle.guam.favorite.service.command.LikeEvent
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.NotificationService
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest.Info
import waffle.guam.favorite.service.infra.Post
import waffle.guam.favorite.service.model.Like
import waffle.guam.favorite.service.query.LikeCountStore

interface LikeSaga {
    suspend fun handleEvent(event: LikeEvent)
}

@Service
class LikeSagaImpl(
    private val likeCountStore: LikeCountStore.Mutable,
    private val notification: NotificationService,
    private val community: CommunityService,
) : LikeSaga {
    override suspend fun handleEvent(event: LikeEvent) {
        val post = community.getPost(event.like.postId) ?: throw RuntimeException("POST NOT FOUND")

        when (event) {
            is LikeCreated -> {
                // push
                notification.notify(CreateNotificationRequest(like = event.like, post = post))
                // increment like
                likeCountStore.increment(boardId = post.boardId, postId = post.id, delta = 1.0)
            }
            is LikeDeleted -> {
                likeCountStore.increment(boardId = post.boardId, postId = post.id, delta = -1.0)
            }
        }
    }
}

private fun CreateNotificationRequest(like: Like, post: Post): CreateNotificationRequest =
    CreateNotificationRequest(
        producerId = like.userId,
        infos = listOf(
            Info(
                consumerId = post.userId,
                kind = "POST_LIKE",
                body = post.title,
                linkUrl = "/api/v1/posts/${post.id}",
                isAnonymousEvent = post.isAnonymous
            )
        )
    )
