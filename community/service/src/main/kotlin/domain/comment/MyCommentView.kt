package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.favorite.api.model.CommentFavoriteInfo
import java.time.Instant

data class MyCommentView(
    val id: Long,
    val postId: Long,
    val content: String,
    val imageCount: Int,
    val likeCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun MyCommentView(comment: PostCommentEntity, favorite: CommentFavoriteInfo) =
    MyCommentView(
        id = comment.id,
        postId = comment.post.id,
        content = comment.content,
        imageCount = comment.images.size,
        likeCount = favorite.count,
        createdAt = comment.createdAt,
        updatedAt = comment.updatedAt
    )
