package waffle.guam.community.data.jdbc.project.workspace

import waffle.guam.community.data.jdbc.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "threads")
class ThreadEntity(
    val projectId: Long,
    val userId: Long,
    val content: String,
): BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
