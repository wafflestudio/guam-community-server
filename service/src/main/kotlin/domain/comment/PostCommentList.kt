package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.PostId

data class PostCommentList(
    val postId: PostId,
    val content: List<PostComment>
) {
    companion object {
        fun of(e: PostEntity) = PostCommentList(
            postId = e.id,
            content = e.comments.map { PostComment.of(it) }
        )
    }
}
