package waffle.guam.community.service.domain.comment

import waffle.guam.community.service.PostId

data class PostCommentDetailList(
    val postId: PostId,
    val content: List<PostCommentDetail>,
)
