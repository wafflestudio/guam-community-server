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
import waffle.guam.community.service.FavoriteService
import waffle.guam.community.service.PostId
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.UserService
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.comment.PostCommentDetail
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostDetail
import waffle.guam.community.service.query.comment.PostCommentListCollector

@Service
class PostDetailService(
    private val postRepository: PostRepository,
    private val userService: UserService,
    private val favoriteService: FavoriteService,
    private val postCommentListCollector: PostCommentListCollector.CacheImpl,
) {
    fun getDetail(userId: UserId, postId: PostId): PostDetail {
        val postSpec = fetchCategories() * postId(postId) * status(PostEntity.Status.VALID)

        return postRepository.findOne(spec = postSpec)
            ?.fillData(userId)
            ?: throw PostNotFound(postId)
    }

    fun PostEntity.fillData(callerId: Long): PostDetail = runBlocking {
        val favorite = async { favoriteService.getPostFavorite(userId = userId, postId = id) }
        val commentList = async { postCommentListCollector.get(id = id) }
        val commentIds = commentList.await().content.map { it.id }
        val commentFavorite = async { favoriteService.getCommentFavorite(userId = callerId, commentIds = commentIds) }.await()
        val comments = commentList.await().content.map { PostCommentDetail(it, commentFavorite[it.id]!!, callerId) }

        PostDetail.of(
            post = Post(this@fillData),
            user = userService.get(id = userId),
            category = categories.singleOrNull()?.let(::PostCategory),
            comments = comments,
            favorite = favorite.await(),
            callerId = callerId,
        )
    }
}