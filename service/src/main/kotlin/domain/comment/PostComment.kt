package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostComment(
    val id: Long,
    val postId: PostId,
    val user: User,
    val content: String,
    val imagePaths: List<String>,
    val mentionIds: List<Long>,
    val likeCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
)

fun PostComment(e: PostCommentEntity, likeCount: Int? = null) =
    PostComment(
        postId = e.post.id,
        id = e.id,
        user = User(e.user),
        content = e.content,
        imagePaths = e.images,
        mentionIds = e.mentionIds,
        likeCount = likeCount ?: e.likes.size,
        createdAt = e.createdAt,
        updatedAt = e.updatedAt,
    )
