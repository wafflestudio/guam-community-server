package waffle.guam.community.service.domain.scrap

import waffle.guam.community.service.PostId

data class PostScrapList(
    val postId: PostId,
    val content: List<PostScrap>
)
