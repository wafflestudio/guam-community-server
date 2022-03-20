package waffle.guam.community.controller.comment.req

import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.comment.CreatePostComment

data class CreatePostCommentRequest(
    val content: String,
    val images: List<MultipartFile>,
    val mentionIds: List<UserId>,
) {
    fun toCommand(postId: Long, userId: Long): CreatePostComment = CreatePostComment(
        postId = postId,
        userId = userId,
        content = content,
        images = images,
        mentionIds = mentionIds
    )
}
