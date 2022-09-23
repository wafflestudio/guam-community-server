package waffle.guam.community.service.query.comment

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.post.fetchComments
import waffle.guam.community.data.jdbc.post.postId
import waffle.guam.community.data.jdbc.times
import waffle.guam.community.service.PostId
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.client.FavoriteService
import waffle.guam.community.service.domain.comment.PostCommentDetail
import waffle.guam.community.service.domain.comment.PostCommentDetailList
import waffle.guam.community.service.domain.comment.PostCommentList
import waffle.guam.user.client.GuamUserClient

@Service
class PostCommentService(
    private val postRepository: PostRepository,
    private val userClient: GuamUserClient.Blocking,
    private val favoriteService: FavoriteService,
) {

    fun fetchPostCommentList(postId: PostId, callerId: UserId): PostCommentDetailList {
        val commentList = fetchList(id = postId)
        val commentIds = commentList.content.map { it.id }
        val commentFavorite = favoriteService.getCommentFavorite(userId = callerId, commentIds = commentIds)
        val commentDetailList = commentList.content.map { PostCommentDetail(it, commentFavorite[it.id]!!, callerId) }

        return PostCommentDetailList(
            postId = postId,
            commentList = commentDetailList,
            writerId = commentList.writerId,
            isAnonymous = commentList.isAnonymousPost,
        )
    }

    private fun fetchList(id: PostId): PostCommentList {
        val post = postRepository.findOne(spec = postId(id) * fetchComments()) ?: throw PostNotFound()
        val userMap = userClient.getUsers(post.comments.map { it.userId })
        return PostCommentList(post, userMap)
    }
}
