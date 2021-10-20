package waffle.guam.community.service.domain.like

import waffle.guam.community.data.jdbc.like.PostCommentLikeEntity

data class PostCommentLike(
    val postCommentId: Long,
    val userId: Long
) {
    companion object {
        fun of(e: PostCommentLikeEntity) = PostCommentLike(postCommentId = e.comment.id, userId = e.userId)
    }
}