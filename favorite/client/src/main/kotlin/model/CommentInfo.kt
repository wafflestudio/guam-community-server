package waffle.guam.favorite.client.model

data class CommentInfo(
    val postCommentId: Long,
    val count: Int,
    val like: Boolean,
)
