package waffle.guam.community.data.jdbc.post.projection

import waffle.guam.community.data.jdbc.post.PostEntity
import java.time.Instant

data class MyPostView(
    val id: Long,
    val boardId: Long,
    val title: String,
    val content: String,
    val imagePaths: List<String>,
    val likeCount: Long,
    val commentCount: Long,
    val status: PostEntity.Status,
    val createdAt: Instant,
    val updatedAt: Instant,
)
