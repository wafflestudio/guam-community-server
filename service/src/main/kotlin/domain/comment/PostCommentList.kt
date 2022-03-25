package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.like.PostCommentLikeList

data class PostCommentList(
    val postId: PostId,
    val writerId: UserId,
    val boardId: BoardId,
    val content: List<PostComment>,
) {
    val isAnonymousPost: Boolean
        get() = boardId == 1L
}

fun PostCommentList(e: PostEntity, likeMap: Map<Long, PostCommentLikeList>): PostCommentList {
    return PostCommentList(
        postId = e.id,
        writerId = e.user.id,
        boardId = e.boardId,
        content = e.comments
            .filter { comment ->
                comment.status == PostCommentEntity.Status.VALID
            }
            .map { comment ->
                val likeCount = likeMap[comment.id]?.content?.size ?: 0
                PostComment(comment, likeCount)
            }
            .sortedBy { comment ->
                comment.id
            }
    )
}
