package waffle.guam.community.service.query.post.displayer

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostAPIRepository
import waffle.guam.community.isNull
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostDetail
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.domain.post.PostPreview
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.community.service.query.category.PostCategoryListCollector
import waffle.guam.community.service.query.comment.PostCommentListCollector
import waffle.guam.community.service.query.like.PostLikeListCollector
import waffle.guam.community.service.query.post.PostCollector
import waffle.guam.community.service.query.post.PostListCollector
import waffle.guam.community.service.query.post.RecentPostListCollector
import waffle.guam.community.service.query.post.SearchedPostListCollector
import waffle.guam.community.service.query.scrap.PostScrapListCollector
import waffle.guam.community.service.query.user.UserCollector

@Service
class PostDisplayer(
    private val postAPIRepository: PostAPIRepository,
    private val postListCollector: PostListCollector,
    private val searchedPostListCollector: SearchedPostListCollector,
    private val recentPostListCollector: RecentPostListCollector.CacheImpl,
    private val postCollector: PostCollector.CacheImpl,
    private val userCollector: UserCollector.CacheImpl,
    private val postCategoryListCollector: PostCategoryListCollector.CacheImpl,
    private val postLikeListCollector: PostLikeListCollector.CacheImpl,
    private val postScrapListCollector: PostScrapListCollector.CacheImpl,
    private val postCommentListCollector: PostCommentListCollector.CacheImpl,
) {
    fun getPostPreviewList(
        boardId: Long,
        userId: Long,
        beforePostId: Long? = null,
        page: Int? = null,
    ): PostPreviewList {
        // page 값이 존재하는 경우, beforePostId는 null이어야 함
        // page 값이 null인 경우, beforePostId는 어떤 값이든 상관 없음
        require(page.isNull || beforePostId.isNull)

        return if (beforePostId.isNull && (page == 0 || page == null)) {
            // Cache for recent posts
            recentPostListCollector.get(boardId).fillData(userId)
        } else {
            // No cache for old posts
            val query = PostListCollector.Query(
                boardId = boardId.takeIf { it > 0L },
                beforePostId = beforePostId,
                page = page ?: 0,
                size = 20
            )
            postListCollector.get(query).fillData(userId)
        }
    }

    fun getSearchedPostPreviewList(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        beforePostId: Long?,
    ): PostPreviewList =
        // No cache for searched posts
        searchedPostListCollector.get(
            SearchedPostListCollector.Query(
                categoryId = categoryId,
                keyword = keyword,
                beforePostId = beforePostId,
                size = 20
            )
        ).fillData(userId)

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
        val userMap = async { userCollector.multiGet(content.map { it.userId }) }
        val categoryMap = async { postCategoryListCollector.multiGet(content.map { it.id }) }
        val likeMap = async { postLikeListCollector.multiGet(content.map { it.id }) }
        val commentMap = async { postCommentListCollector.multiGet(content.map { it.id }) }
        val scrapMap = async { postScrapListCollector.multiGet(content.map { it.id }) }

        PostPreviewList(
            content = content.map {
                PostPreview.of(
                    post = it,
                    user = userMap.await()[it.userId]!!,
                    category = categoryMap.await()[it.id]?.content?.singleOrNull(),
                    likes = likeMap.await()[it.id]?.content,
                    scraps = scrapMap.await()[it.id]?.content,
                    comments = commentMap.await()[it.id]?.content,
                    callerId = callerId,
                )
            },
            hasNext = hasNext
        )
    }

    private fun Post.fillData(callerId: Long): PostDetail = runBlocking {
        val commentList = async { postCommentListCollector.get(id = id) }
        val likes = async { postLikeListCollector.get(id = id) }
        val scraps = async { postScrapListCollector.get(id = id) }
        val categories = async { postCategoryListCollector.get(id = id) }

        PostDetail.of(
            post = this@fillData,
            user = userCollector.get(id = userId),
            category = categories.await().content.singleOrNull(),
            likes = likes.await().content,
            comments = commentList.await().content,
            scraps = scraps.await().content,
            callerId = callerId,
        )
    }
}
