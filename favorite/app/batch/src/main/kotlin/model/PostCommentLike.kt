package waffle.guam.favorite.batch.model
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "post_comment_likes")
@Entity
class PostCommentLike(
    val postCommentId: Long,
    val userId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}

data class PostCommentLikeCount(
    val postCommentId: Long,
    val count: Long,
)
