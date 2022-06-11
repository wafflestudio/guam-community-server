package waffle.guam.user.infra.db

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "push_events")
@Entity
class NotificationEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,
    val userId: Long,
    @JoinColumn(name = "writer_id")
    @ManyToOne
    val writer: UserEntity,
    @Enumerated(value = EnumType.STRING)
    val kind: NotificationKind,
    val body: String,
    val linkUrl: String,
    val isAnonymousEvent: Boolean,
    var isRead: Boolean = false,
    val createdAt: Instant = Instant.now()
)

enum class NotificationKind {
    POST_LIKE, POST_COMMENT, POST_COMMENT_MENTION, POST_COMMENT_LIKE, POST_SCRAP
}
