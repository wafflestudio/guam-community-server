package waffle.guam.community.service.domain.category

import waffle.guam.community.service.CategoryId
import waffle.guam.community.service.PostId

data class PostCategory(
    val postId: PostId,
    val categoryId: CategoryId,
    val title: String
)
