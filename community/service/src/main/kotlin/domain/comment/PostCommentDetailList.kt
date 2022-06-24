package waffle.guam.community.service.domain.comment

import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.user.AnonymousUser

data class PostCommentDetailList(
    val postId: PostId,
    val content: List<PostCommentDetail>,
)

fun PostCommentDetailList(
    postId: Long,
    commentList: List<PostCommentDetail>,
    writerId: Long,
    isAnonymous: Boolean,
): PostCommentDetailList {
    if (!isAnonymous) return PostCommentDetailList(postId, commentList)

    val userIdOrder = commentList
        .filter { it.user.id != writerId }
        .sortedBy { it.createdAt }
        .distinctBy { it.user.id }
        .mapIndexed { idx, comment -> comment.user.id to idx + 1 }
        .toMap()

    return PostCommentDetailList(
        postId,
        commentList.map {
            val suffix = userIdOrder[it.user.id] ?: "(글쓴이)"
            it.copy(user = AnonymousUser(suffix.toString()), mentionIds = listOf()) // todo 익명 멘션
        }
    )
}
