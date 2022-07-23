package waffle.guam.community.controller.post.req

import waffle.guam.community.service.command.post.CreatePost

data class CreatePostRequest(
    val boardId: Long,
    val title: String,
    val content: String,
    val categoryId: Long,
    val imageFilePaths: List<String>?,
) {
    fun toCommand(userId: Long): CreatePost = CreatePost(
        userId = userId,
        boardId = boardId,
        title = title,
        content = content,
        imageFilePaths = imageFilePaths ?: listOf(),
        categoryId = categoryId,
    )
}
