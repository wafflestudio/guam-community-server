package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostComment(
    val id: Long,
    val postId: Long,
    val user: User,
    val content: String,
    val imagePaths: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun of(e: PostCommentEntity): PostComment = PostComment(
            id = e.id,
            postId = e.post.id,
            user = User.of(e.user),
            content = e.content,
            imagePaths = e.images,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt
        )
    }
}
