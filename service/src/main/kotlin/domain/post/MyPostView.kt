package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import java.time.Instant
import waffle.guam.community.data.jdbc.post.projection.MyPostView as Projection

data class MyPostView(
    val id: PostId,
    val boardId: BoardId,
    val title: String,
    val content: String,
    val imageCount: Int,
    val likeCount: Long,
    val commentCount: Long,
    val status: PostEntity.Status,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun of(projection: Projection): MyPostView =
            MyPostView(
                id = projection.id,
                boardId = projection.boardId,
                title = projection.title,
                content = projection.content,
                imageCount = projection.imagePaths.size,
                likeCount = projection.likeCount,
                commentCount = projection.commentCount,
                status = projection.status,
                createdAt = projection.createdAt,
                updatedAt = projection.updatedAt,
            )

        fun listOf(l: List<Projection>): List<MyPostView> =
            l.map { projectionData -> MyPostView.of(projectionData) }
    }
}
