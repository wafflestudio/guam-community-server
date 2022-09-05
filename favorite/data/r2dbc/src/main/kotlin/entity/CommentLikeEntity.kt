package waffle.guam.favorite.data.r2dbc.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("post_comment_likes")
data class CommentLikeEntity(
    @Id
    val id: Long = 0L,
    val postCommentId: Long,
    val userId: Long,
)
