package waffle.guam.community.service.domain.like

data class PostLikeList(
    val postId: Long,
    val content: List<PostLike>,
)
