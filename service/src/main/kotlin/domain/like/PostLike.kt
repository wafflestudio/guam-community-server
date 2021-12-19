package waffle.guam.community.service.domain.like

import waffle.guam.community.data.jdbc.like.PostLikeEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId

data class PostLike(
    val postId: PostId,
    val userId: UserId,
)

fun PostLike(e: PostLikeEntity) = PostLike(
    postId = e.post.id,
    userId = e.user.id
)
