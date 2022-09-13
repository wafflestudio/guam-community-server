package waffle.guam.community.data.jdbc.report

import waffle.guam.community.data.jdbc.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "reports",
    uniqueConstraints = [UniqueConstraint(columnNames = ["postId", "reportedUserId"])]
)
class ReportEntity(
    val postId: Long,
    val reportedUserId: Long,
    val reason: String,
): BaseTimeEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
