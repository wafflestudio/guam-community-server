package waffle.guam.community.data.jdbc.push

import waffle.guam.community.data.jdbc.user.UserEntity
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
data class PushEventEntity(
    val userId: Long,

    @ManyToOne
    @JoinColumn(name = "writer_id")
    val writer: UserEntity,

    @Enumerated(value = EnumType.STRING)
    val kind: Kind,

    val body: String,

    val linkUrl: String,

    val isAnonymousEvent: Boolean,
) {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L

    var isRead: Boolean = false

    val createdAt: Instant = Instant.now()

    enum class Kind {
        POST_LIKE, POST_COMMENT, POST_COMMENT_MENTION, POST_COMMENT_LIKE, POST_SCRAP
    }
}
