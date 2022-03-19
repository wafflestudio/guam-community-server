package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostComment(
    val id: Long,
    val postId: PostId,
    val user: User,
    val content: String,
    val imagePaths: List<String>,
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
        likeCount = likeCount ?: e.likes.size,
        createdAt = e.createdAt,
        updatedAt = e.updatedAt,
    )

fun AnonymousComments(commentList: List<PostComment>, writerId: Long): List<PostComment> {
    val userIdOrder = commentList
        .filter { it.user.id != writerId }
        .sortedBy { it.createdAt }
        .distinctBy { it.user.id }
        .mapIndexed { idx, comment -> comment.user.id to idx + 1 }
        .toMap()

    return commentList.map {
        val suffix = userIdOrder[it.user.id] ?: "(글쓴이)"
        it.copy(user = AnonymousUser(suffix.toString()))
    }
}
