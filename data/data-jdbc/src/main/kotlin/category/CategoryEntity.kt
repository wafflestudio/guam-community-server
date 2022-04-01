package waffle.guam.community.data.jdbc.category

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "categories")
@Entity
data class CategoryEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    val title: String,
)
