package waffle.guam.community.service.domain.category

import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.service.CategoryId
import waffle.guam.community.service.PostId

data class PostCategory(
    val postId: PostId,
    val categoryId: CategoryId,
    val title: String
)

fun PostCategory(e: PostCategoryEntity) = PostCategory(e.post.id, e.category.id, e.category.title)
