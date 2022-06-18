package waffle.guam.community.service.query.post.displayer

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostAPIRepository
import waffle.guam.community.service.FavoriteService
import waffle.guam.community.service.UserId
import waffle.guam.community.service.UserService
import waffle.guam.community.service.domain.comment.PostCommentDetail
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostDetail
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.domain.post.PostPreview
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.community.service.isNull
import waffle.guam.community.service.query.category.PostCategoryListCollector
import waffle.guam.community.service.query.comment.PostCommentListCollector
import waffle.guam.community.service.query.post.PostCollector
import waffle.guam.community.service.query.post.PostListCollector
import waffle.guam.community.service.query.post.RecentPostListCollector
import waffle.guam.community.service.query.post.SearchedPostListCollector

@Service
class PostDisplayer(
    private val postAPIRepository: PostAPIRepository,
    private val postListCollector: PostListCollector,
    private val searchedPostListCollector: SearchedPostListCollector,
    private val recentPostListCollector: RecentPostListCollector.CacheImpl,
    private val postCollector: PostCollector.CacheImpl,
    private val userService: UserService,
    private val favoriteServce: FavoriteService,
    private val postCategoryListCollector: PostCategoryListCollector.CacheImpl,
    private val postCommentListCollector: PostCommentListCollector.CacheImpl,
    private val postPreviewService: PostPreviewService,
) {
    fun getPostPreviewList(
        boardId: Long?,
        userId: Long,
        beforePostId: Long? = null,
        page: Int? = null,
    ): PostPreviewList {
        // page 값이 존재하는 경우, beforePostId는 null이어야 함
        // page 값이 null인 경우, beforePostId는 어떤 값이든 상관 없음
        require(page.isNull || beforePostId.isNull)

        return if (page != null) {
            postPreviewService.getRecentPreviews(userId = userId, boardId = boardId, page = page)
        } else {
            postPreviewService.getRecentPreviews(userId = userId, boardId = boardId, before = beforePostId)
        }
    }

    fun getSearchedPostPreviewList(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        beforePostId: Long?,
    ): PostPreviewList =
        postPreviewService.getSearchedPostPreview(
            userId = userId,
            categoryId = categoryId,
            keyword = keyword,
            before = beforePostId,
        )

    fun getPostDetail(postId: Long, userId: Long): PostDetail =
        postCollector.get(postId).fillData(userId)

    fun getUserPostList(
        userId: Long,
        beforePostId: Long?,
    ): PostPreviewList {
        val posts = PostList(
            content = postAPIRepository.findPostsOfUser(userId = userId, beforePostId = beforePostId).map { Post(it) },
            hasNext = true // fixme 페이징
        )
        return posts.fillData(userId)
    }

    private fun PostList.fillData(callerId: UserId): PostPreviewList = runBlocking {
        val userMap = async { userService.multiGet(content.map { it.userId }) }
        val categoryMap = async { postCategoryListCollector.multiGet(content.map { it.id }) }
        val commentMap = async { postCommentListCollector.multiGet(content.map { it.id }) }
        val favoriteMap = async { favoriteServce.getPostFavorite(callerId, content.map { it.id }) }

        PostPreviewList(
            content = content.map {
                PostPreview.of(
                    post = it,
                    user = userMap.await()[it.userId]!!,
                    category = categoryMap.await()[it.id]?.content?.singleOrNull(),
                    comments = commentMap.await()[it.id]?.content,
                    favorite = favoriteMap.await()[it.id]!!,
                    callerId = callerId,
                )
            },
            hasNext = hasNext
        )
    }

    private fun Post.fillData(callerId: Long): PostDetail = runBlocking {
        val categories = async { postCategoryListCollector.get(id = id) }
        val favorite = async { favoriteServce.getPostFavorite(userId = callerId, postId = id) }

        val commentList = async { postCommentListCollector.get(id = id) }
        val commentIds = commentList.await().content.map { it.id }
        val commentFavorite = async { favoriteServce.getCommentFavorite(userId = callerId, commentIds = commentIds) }.await()
        val comments = commentList.await().content.map { PostCommentDetail(it, commentFavorite[it.id]!!, callerId) }

        PostDetail.of(
            post = this@fillData,
            user = userService.get(id = userId),
            category = categories.await().content.singleOrNull(),
            comments = comments,
            favorite = favorite.await(),
            callerId = callerId,
        )
    }
}
