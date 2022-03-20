package waffle.guam.community.service.domain.comment

import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostCommentDetail(
    val id: Long,
    val postId: PostId,
    val user: User,
    val content: String,
    val imagePaths: List<String>,
    val likeCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isMine: Boolean,
)

fun PostCommentDetail(d: PostComment, callerId: UserId) =
    PostCommentDetail(
        postId = d.postId,
        id = d.id,
        user = d.user,
        content = d.content,
        imagePaths = d.imagePaths,
        likeCount = d.likeCount,
        createdAt = d.createdAt,
        updatedAt = d.updatedAt,
        isMine = d.user.id == callerId,
    )

fun AnonymousComments(commentList: List<PostCommentDetail>, writerId: Long): List<PostCommentDetail> {
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
