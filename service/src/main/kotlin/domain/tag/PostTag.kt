package waffle.guam.community.service.domain.tag

import waffle.guam.community.service.PostId
import waffle.guam.community.service.TagId

data class PostTag(
    val postId: PostId,
    val tagId: TagId,
    val title: String
)
