package waffle.guam.community.service.domain.push

import waffle.guam.community.data.jdbc.push.PushEventEntity
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PushEvent(
    val id: Long = 0L,
    val userId: Long,
    val writer: User,
    val kind: String,
    val body: String,
    val linkUrl: String,
    val isRead: Boolean,
    val createdAt: Instant,
)

fun PushEvent(e: PushEventEntity): PushEvent = PushEvent(
    id = e.id,
    userId = e.userId,
    writer = User(e.writer),
    kind = e.kind.name,
    body = e.body,
    linkUrl = e.linkUrl,
    isRead = e.isRead,
    createdAt = e.createdAt
)
