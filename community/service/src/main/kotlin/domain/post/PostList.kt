package waffle.guam.community.service.domain.post

import org.springframework.data.domain.Page
import waffle.guam.community.data.jdbc.post.PostEntity

data class PostList(
    val content: List<Post>,
    val hasNext: Boolean,
)

fun PostList(list: Page<PostEntity>) = PostList(
    content = list.content.map { Post(it) },
    hasNext = list.hasNext()
)
