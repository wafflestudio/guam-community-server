package waffle.guam.user.notification.event

import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification.Info

internal data class PostCommentCreated(
    val postId: Long,
    val postUserId: Long,
    val mentionIds: List<Long>,
    val content: String,
    val isAnonymous: Boolean,
    val writerId: Long,
) : NotifyingEvent {
    override fun toRequest() = CreateNotification(
        producerId = writerId,
        infos = notificationInfos,
    )

    private val notificationInfos: List<Info> get() {
        val createdInfo = listOf(
            Info(
                consumerId = postUserId,
                kind = "POST_COMMENT",
                body = content,
                linkUrl = "/api/v1/posts/$postId",
                isAnonymousEvent = isAnonymous,
            )
        )

        val mentionInfos = mentionIds.map { consumerId ->
            Info(
                consumerId = consumerId,
                kind = "POST_COMMENT_MENTION",
                body = content,
                linkUrl = "/api/v1/posts/$postId",
                isAnonymousEvent = isAnonymous,
            )
        }

        return createdInfo + mentionInfos
    }
}
