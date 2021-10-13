package waffle.guam.community.service.domain.post

data class PostList(
    val content: List<Post>,
    val hasNext: Boolean
)
