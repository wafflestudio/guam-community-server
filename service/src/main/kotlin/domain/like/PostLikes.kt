package waffle.guam.community.service.domain.like

data class PostLikes(
    val postId: Long,
    val userIds: List<Long>
)
