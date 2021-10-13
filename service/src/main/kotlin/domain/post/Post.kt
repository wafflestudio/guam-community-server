package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.post.PostEntity
import java.time.Instant

data class Post(
    val id: Long,
    val boardId: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun of(e: PostEntity): Post = Post(
            id = e.id,
            boardId = e.boardId,
            userId = e.userId,
            title = e.title,
            content = e.content,
            status = e.status.name,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt
        )
    }
}
