package waffle.guam.community.service.domain.like

import waffle.guam.community.data.jdbc.like.PostLikeEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId

data class PostLike(
    val postId: PostId,
    val userId: UserId,
) {
    companion object {
        fun of(e: PostLikeEntity) = PostLike(
            postId = e.post.id,
            userId = e.userId
        )
    }
}
