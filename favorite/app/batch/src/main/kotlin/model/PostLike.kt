package waffle.guam.favorite.batch.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "post_likes")
@Entity
class PostLike(
    val postId: Long,
    val userId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

data class PostLikeCount(
    val postId: Long,
    val count: Long
)
