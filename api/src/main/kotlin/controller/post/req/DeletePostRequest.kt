package waffle.guam.community.controller.post.req

import waffle.guam.community.service.command.post.DeletePost

data class DeletePostRequest(
    val postId: Long,
) {
    fun toCommand(userId: Long): DeletePost = DeletePost(
        postId = postId,
        userId = userId
    )
}
