package waffle.guam.community.data.jdbc.like

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "post_likes")
@Entity
data class PostLikeEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long,
    val userId: Long,
    val postId: Long
)
