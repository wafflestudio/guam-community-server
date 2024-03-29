package waffle.guam.community.service.query.post

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.post.fetchCategories
import waffle.guam.community.data.jdbc.post.postId
import waffle.guam.community.data.jdbc.post.status
import waffle.guam.community.data.jdbc.times
import waffle.guam.community.service.PostId
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostDetail
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.query.comment.PostCommentService
import waffle.guam.favorite.client.GuamFavoriteClient
import waffle.guam.user.client.GuamUserClient

@Service
class PostDetailService(
    private val postRepository: PostRepository,
    private val userClient: GuamUserClient.Blocking,
    private val favoriteClient: GuamFavoriteClient.Blocking,
    private val postCommentService: PostCommentService,
) {
    fun getDetail(userId: UserId, postId: PostId): PostDetail {
        val postSpec = fetchCategories() * postId(postId) * status(PostEntity.Status.VALID)

        return postRepository.findOne(spec = postSpec)
            ?.fillData(userId)
            ?: throw PostNotFound(postId)
    }

    private fun PostEntity.fillData(callerId: Long): PostDetail = runBlocking {
        val favorite = async { favoriteClient.getPostInfo(userId = callerId, postId = id) }
        val comments = async { postCommentService.fetchPostCommentList(callerId = callerId, postId = id) }
        val user = if (isAnonymous) AnonymousUser() else userClient.getUser(userId = userId)

        PostDetail(
            post = Post(this@fillData),
            user = user,
            category = categories.singleOrNull()?.let(::PostCategory),
            comments = comments.await().content,
            favorite = favorite.await(),
            callerId = callerId,
        )
    }
}
