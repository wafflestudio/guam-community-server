package waffle.guam.community.service.domain.like

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.PostId

data class PostLikeList(
    val postId: PostId,
    val content: List<PostLike>,
) {
    companion object {
        fun of(e: PostEntity) = PostLikeList(
            postId = e.id,
            content = e.likes.map { PostLike.of(it) }
        )
    }
}
