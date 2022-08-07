package waffle.guam.favorite.batch.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "post_scraps")
@Entity
class PostScrap(
    val postId: Long,
    val userId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

data class PostScrapCount(
    val postId: Long,
    val count: Long
)
