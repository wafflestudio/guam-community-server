package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import java.time.Instant

data class Post(
    val id: PostId,
    val boardId: BoardId,
    val userId: UserId,
    val title: String,
    val content: String,
    val imagePaths: List<String>,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val isImageIncluded: Boolean
        get() = imagePaths.isNotEmpty()
}

fun Post(e: PostEntity) = Post(
    id = e.id,
    boardId = e.boardId,
    userId = e.user.id,
    title = e.title,
    content = e.content,
    imagePaths = e.images,
    status = e.status.name,
    createdAt = e.createdAt,
    updatedAt = e.updatedAt
)
