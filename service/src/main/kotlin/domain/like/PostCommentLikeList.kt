package waffle.guam.community.service.domain.like

import waffle.guam.community.data.jdbc.comment.PostCommentEntity

data class PostCommentLikeList(
    val postCommentId: Long,
    val content: List<PostCommentLike>
)

fun PostCommentLikeList(e: PostCommentEntity) = PostCommentLikeList(
    postCommentId = e.id,
    content = e.likes.map { PostCommentLike(it) }
)
