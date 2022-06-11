package waffle.guam.user.service.notification

import waffle.guam.user.infra.db.NotificationEntity
import waffle.guam.user.service.user.User
import java.time.Instant

data class Notification(
    val id: Long = 0L,
    val userId: Long,
    val writer: User,
    val kind: String,
    val body: String,
    val linkUrl: String,
    val isRead: Boolean,
    val createdAt: Instant,
)

data class NotificationsPage(
    val userId: Long,
    val content: List<Notification>,
    val hasNext: Boolean,
)

fun Notification(e: NotificationEntity): Notification = Notification(
    id = e.id,
    userId = e.userId,
    writer = User(e.writer),
    kind = e.kind.name,
    body = e.body,
    linkUrl = e.linkUrl,
    isRead = e.isRead,
    createdAt = e.createdAt,
)
