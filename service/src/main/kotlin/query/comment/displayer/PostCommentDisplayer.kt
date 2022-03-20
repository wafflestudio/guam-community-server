package waffle.guam.community.service.query.comment.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.comment.AnonymousComments
import waffle.guam.community.service.domain.comment.PostCommentDetail
import waffle.guam.community.service.domain.comment.PostCommentDetailList
import waffle.guam.community.service.domain.comment.PostCommentList
import waffle.guam.community.service.query.comment.PostCommentListCollector

@Service
class PostCommentDisplayer(
    private val postCommentListCollector: PostCommentListCollector.CacheImpl,
) {
    fun getPostCommentList(
        postId: PostId,
        userId: UserId,
    ): PostCommentDetailList {
        return postCommentListCollector.get(postId).fillData(userId)
    }

    fun PostCommentList.fillData(callerId: UserId): PostCommentDetailList {
        val detailList = content.map { PostCommentDetail(it, callerId) }
        return PostCommentDetailList(
            postId = postId,
            content = if (isAnonymousPost) AnonymousComments(detailList, writerId) else detailList
        )
    }
}
