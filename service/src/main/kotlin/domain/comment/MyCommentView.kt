package waffle.guam.community.service.domain.comment

import java.time.Instant
import waffle.guam.community.data.jdbc.comment.projection.MyCommentView as Projection

data class MyCommentView(
    val id: Long,
    val postId: Long,
    val content: String,
    val imageCount: Int,
    val likeCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun of(p: Projection): MyCommentView =
            MyCommentView(
                id = p.id,
                postId = p.postId,
                content = p.content,
                imageCount = p.imagePaths.size,
                likeCount = p.likeCount,
                createdAt = p.createdAt,
                updatedAt = p.updatedAt,
            )

        fun listOf(l: List<Projection>): List<MyCommentView> =
            l.map { data -> MyCommentView.of(data) }
    }
}
