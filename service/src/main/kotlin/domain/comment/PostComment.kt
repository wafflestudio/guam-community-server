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
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun of(e: PostCommentEntity) = PostComment(
            postId = e.post.id,
            id = e.id,
            user = User(id = e.user.id, username = e.user.username),
            content = e.content,
            imagePaths = e.images,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt,
        )
    }
}
