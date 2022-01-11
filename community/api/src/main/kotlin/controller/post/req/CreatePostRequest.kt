package waffle.guam.community.controller.post.req

import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.service.command.post.CreatePost

data class CreatePostRequest(
    val boardId: Long,
    val title: String,
    val content: String,
    val tagId: Long,
    val images: List<MultipartFile>
) {
    fun toCommand(userId: Long): CreatePost = CreatePost(
        userId = userId,
        boardId = boardId,
        title = title,
        content = content,
        images = images,
        tagId = tagId
    )
}
