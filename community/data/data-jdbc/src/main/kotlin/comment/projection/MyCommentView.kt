package waffle.guam.community.data.jdbc.comment.projection

import java.time.Instant

data class MyCommentView(
    val id: Long,
    val postId: Long,
    val content: String,
    val imagePaths: List<String>,
    val likeCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)
