package waffle.guam.community.service.domain.comment

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.like.PostCommentLikeList

data class PostCommentList(
    val postId: PostId,
    val content: List<PostComment>,
)

fun PostCommentList(e: PostEntity, likeMap: Map<Long, PostCommentLikeList>) =
    PostCommentList(
        postId = e.id,
        content = e.comments.map { comment ->
            val likeCount = likeMap[comment.id]?.content?.size ?: 0
            PostComment(comment, likeCount)
        }
    )
