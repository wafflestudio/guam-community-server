package waffle.guam.community.controller.comment.req

import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.comment.CreatePostComment

data class CreatePostCommentRequest(
    val content: String,
    val mentionIds: List<UserId>?,
    val imageFilePaths: List<String>?,
) {
    fun toCommand(postId: Long, userId: Long): CreatePostComment = CreatePostComment(
        postId = postId,
        userId = userId,
        content = content,
        imageFilePaths = imageFilePaths ?: listOf(),
        mentionIds = mentionIds ?: listOf(),
    )
}
