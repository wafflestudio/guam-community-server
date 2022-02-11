package waffle.guam.community.service.domain.scrap

import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId

data class PostScrap(
    val postId: PostId,
    val userId: UserId,
)
