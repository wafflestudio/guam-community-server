package waffle.guam.community.service.domain.like

import waffle.guam.community.data.jdbc.like.PostLikeEntity

data class PostLike(
    val postId: Long,
    val userId: Long,
) {
    companion object {
        fun of(e: PostLikeEntity): PostLike = PostLike(
            postId = e.post.id,
            userId = e.userId
        )
    }
}
