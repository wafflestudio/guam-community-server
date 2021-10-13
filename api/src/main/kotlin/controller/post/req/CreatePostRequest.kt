package waffle.guam.community.controller.post.req

import waffle.guam.community.service.command.post.CreatePost

data class CreatePostRequest(
    val boardId: Long,
    val title: String,
    val content: String,
    val tagId: Long
) {
    fun toCommand(userId: Long): CreatePost = CreatePost(
        userId = userId,
        boardId = boardId,
        content = content,
        title = title,
        tagId = tagId
    )
}
