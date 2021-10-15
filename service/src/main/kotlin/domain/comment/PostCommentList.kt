package waffle.guam.community.service.domain.comment

data class PostCommentList(
    val postId: Long,
    val content: List<PostComment>
)
