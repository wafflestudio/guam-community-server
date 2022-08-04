package waffle.guam.user.notification.event

import waffle.guam.user.infra.db.NotificationKind
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification.Info

internal data class Scrap(
    val postId: Long,
    val userId: Long,
)

internal data class PostScrapCreated(
    val scrap: Scrap,
    val post: Post,
) : NotifyingEvent {
    override fun toRequest(): CreateNotification {
        return CreateNotification(
            producerId = scrap.userId,
            infos = listOf(
                Info(
                    consumerId = post.userId,
                    kind = NotificationKind.POST_SCRAP.name,
                    body = post.title,
                    linkUrl = "/api/v1/posts/${post.id}",
                    isAnonymousEvent = post.isAnonymous
                )
            )
        )
    }
}
