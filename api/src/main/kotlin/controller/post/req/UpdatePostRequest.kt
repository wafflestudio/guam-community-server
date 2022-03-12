package waffle.guam.community.controller.post.req

import waffle.guam.community.service.command.post.UpdatePost

data class UpdatePostRequest(
    val title: String? = null,
    val content: String? = null,
    val boardId: Long? = null,
    val tagId: Long? = null,
) {
    fun toCommand(postId: Long, userId: Long): UpdatePost {
        return UpdatePost(
            postId = postId,
            userId = userId,
            title = title,
            content = content,
            boardId = boardId,
            tagId = tagId,
        )
    }
}
