package waffle.guam.community.service.domain.tag

data class PostTagList(
    val postId: Long,
    val content: List<PostTag>
)
