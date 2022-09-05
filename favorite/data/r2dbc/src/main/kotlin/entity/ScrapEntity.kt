package waffle.guam.favorite.data.r2dbc.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("post_scraps")
data class ScrapEntity(
    @Id
    val id: Long = 0L,
    val postId: Long,
    val userId: Long,
)
