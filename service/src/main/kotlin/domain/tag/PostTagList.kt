package waffle.guam.community.service.domain.tag

import waffle.guam.community.service.PostId

data class PostTagList(
    val postId: PostId,
    val content: List<PostTag>
)
