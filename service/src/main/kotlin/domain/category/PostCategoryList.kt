package waffle.guam.community.service.domain.category

import waffle.guam.community.service.PostId

data class PostCategoryList(
    val postId: PostId,
    val content: List<PostCategory>
)
