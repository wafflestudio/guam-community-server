package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.user.User

data class PostCommentList(
    val postId: PostId,
    val writerId: UserId,
    val boardId: BoardId,
    val content: List<PostComment>,
) {
    val isAnonymousPost: Boolean
        get() = boardId == 1L
}

fun PostCommentList(
    e: PostEntity,
    userMap: Map<Long, User>,
): PostCommentList {
    return PostCommentList(
        postId = e.id,
        writerId = e.userId,
        boardId = e.boardId,
        content = e.comments
            .filter { comment -> comment.status == PostCommentEntity.Status.VALID }
            .map { comment -> PostComment(comment, userMap[comment.userId]!!) }
            .sortedBy { comment -> comment.id }
    )
}
