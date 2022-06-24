package waffle.guam.community.service.query.comment.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.client.FavoriteService
import waffle.guam.community.service.domain.comment.AnonymousComments
import waffle.guam.community.service.domain.comment.PostCommentDetail
import waffle.guam.community.service.domain.comment.PostCommentDetailList
import waffle.guam.community.service.domain.comment.PostCommentList
import waffle.guam.community.service.query.comment.PostCommentListCollector

@Service
class PostCommentDisplayer(
    private val postCommentListCollector: PostCommentListCollector.CacheImpl,
    private val favoriteService: FavoriteService,
) {
    fun getPostCommentList(
        postId: PostId,
        userId: UserId,
    ): PostCommentDetailList {
        return postCommentListCollector.get(postId).fillData(userId)
    }

    fun PostCommentList.fillData(callerId: UserId): PostCommentDetailList {
        val favoriteMap = favoriteService.getCommentFavorite(userId = callerId, commentIds = content.map { it.id })
        val detailList = content.map { PostCommentDetail(it, favoriteMap[it.id]!!, callerId) }

        return PostCommentDetailList(
            postId = postId,
            content = if (isAnonymousPost) AnonymousComments(detailList, writerId) else detailList
        )
    }
}
