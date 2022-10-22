package waffle.guam.community.data.jdbc.project

import waffle.guam.community.data.jdbc.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "projects")
class ProjectEntity(
    val title: String,
    val description: String,
    val thumbnail: String?,
    val frontHeadcount: Int,
    val backHeadcount: Int,
    val designerHeadcount: Int,
    val recruiting: Boolean = true,

    @Enumerated(EnumType.STRING)
    val due: Due
): BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

enum class Due {
    ONE, THREE, SIX, MORE, UNDEFINED
}
