package waffle.guam.community.data.jdbc.project

import waffle.guam.community.data.jdbc.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "project_likes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["projectId", "userId"])]
)
class ProjectLikeEntity(
    val projectId: Long,
    val userId: Long,
): BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
