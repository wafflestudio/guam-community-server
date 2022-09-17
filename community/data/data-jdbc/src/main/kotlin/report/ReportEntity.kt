package waffle.guam.community.data.jdbc.report

import waffle.guam.community.data.jdbc.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "reports",
    uniqueConstraints = [UniqueConstraint(columnNames = ["reportedUserId", "targetId", "kind"])]
)
class ReportEntity(
    val targetId: Long,
    val reportedUserId: Long,
    val reason: String,
    @Enumerated(EnumType.STRING)
    val kind: Kind,
) : BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    enum class Kind {
        POST, POST_COMMENT
    }
}

fun PostReportEntity(
    postId: Long,
    reportedUser: Long,
    reason: String,
) = ReportEntity(postId, reportedUser, reason, kind = ReportEntity.Kind.POST)

fun PostCommentReportEntity(
    commentId: Long,
    reportedUser: Long,
    reason: String,
) = ReportEntity(commentId, reportedUser, reason, kind = ReportEntity.Kind.POST_COMMENT)
