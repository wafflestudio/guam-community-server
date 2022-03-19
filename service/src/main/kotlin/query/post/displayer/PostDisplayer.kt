package waffle.guam.community.service.query.post.displayer

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostAPIRepository
import waffle.guam.community.isNull
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.post.MyPostView
import waffle.guam.community.service.domain.post.MyPostViewList
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostDetail
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.domain.post.PostPreview
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.community.service.query.comment.PostCommentListCollector
import waffle.guam.community.service.query.like.PostLikeListCollector
import waffle.guam.community.service.query.post.PostCollector
import waffle.guam.community.service.query.post.PostListCollector
import waffle.guam.community.service.query.post.RecentPostListCollector
import waffle.guam.community.service.query.post.SearchedPostListCollector
import waffle.guam.community.service.query.scrap.PostScrapListCollector
import waffle.guam.community.service.query.tag.PostTagListCollector
import waffle.guam.community.service.query.user.UserCollector

@Service
class PostDisplayer(
    private val postAPIRepository: PostAPIRepository,
    private val postListCollector: PostListCollector,
    private val searchedPostListCollector: SearchedPostListCollector,
    private val recentPostListCollector: RecentPostListCollector.CacheImpl,
    private val postCollector: PostCollector.CacheImpl,
    private val userCollector: UserCollector.CacheImpl,
    private val postTagListCollector: PostTagListCollector.CacheImpl,
    private val postLikeListCollector: PostLikeListCollector.CacheImpl,
    private val postScrapListCollector: PostScrapListCollector.CacheImpl,
    private val postCommentListCollector: PostCommentListCollector.CacheImpl,
) {
    fun getPostPreviewList(
        boardId: Long,
        userId: Long,
        afterPostId: Long? = null,
        page: Int? = null,
    ): PostPreviewList {
        return if (afterPostId == 0L || page == 0) {
            // Cache for recent posts
            recentPostListCollector.get(boardId).fillData(userId)
        } else {
            // No cache for old posts
            require(afterPostId.isNull xor page.isNull)
            val query = PostListCollector.Query(
                boardId = boardId.takeIf { it > 0L },
                afterPostId = afterPostId ?: 0,
                page = page ?: 0,
                size = 20
            )
            postListCollector.get(query).fillData(userId)
        }
    }

    fun getSearchedPostPreviewList(
        tagId: Long?,
        keyword: String,
        userId: Long,
        afterPostId: Long?,
    ): PostPreviewList =
        // No cache for searched posts
        searchedPostListCollector.get(
            SearchedPostListCollector.Query(
                tagId = tagId,
                keyword = keyword,
                afterPostId = afterPostId ?: 0L,
                size = 20
            )
        ).fillData(userId)

    fun getPostDetail(postId: Long, userId: Long): PostDetail =
        postCollector.get(postId).fillData(userId)

    fun getUserPostList(
        userId: Long,
        afterPostId: Long?,
        sortByLikes: Boolean,
    ): List<MyPostView> {
        val data =
            postAPIRepository.findPostsOfUser(userId = userId, afterPostId = afterPostId, sortedByLikes = sortByLikes)
        return MyPostViewList(data)
    }

    private fun PostList.fillData(userId: UserId): PostPreviewList = runBlocking {
        val userMap = async { userCollector.multiGet(content.map { it.userId }) }
        val tagMap = async { postTagListCollector.multiGet(content.map { it.id }) }
        val likeMap = async { postLikeListCollector.multiGet(content.map { it.id }) }
        val commentMap = async { postCommentListCollector.multiGet(content.map { it.id }) }
        val scrapMap = async { postScrapListCollector.multiGet(content.map { it.id }) }

        PostPreviewList(
            content = content.map {
                PostPreview(
                    id = it.id,
                    boardId = it.boardId,
                    user = userMap.await()[it.userId]!!,
                    title = it.title,
                    content = it.content,
                    imagePaths = it.imagePaths,
                    status = it.status,
                    categories = tagMap.await()[it.id]?.content ?: emptyList(),
                    likeCount = likeMap.await()[it.id]?.content?.size ?: 0,
                    commentCount = commentMap.await()[it.id]?.content?.size ?: 0,
                    scrapCount = scrapMap.await()[it.id]?.content?.size ?: 0,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    isLiked = likeMap.await()[it.id]?.content?.any { like -> like.userId == userId } ?: false,
                    isScrapped = scrapMap.await()[it.id]?.content?.any { scrap -> scrap.userId == userId } ?: false,
                )
            },
            hasNext = hasNext
        )
    }

    private fun Post.fillData(callerId: Long): PostDetail {
        val commentList = postCommentListCollector.get(id = id)
        val likes = postLikeListCollector.get(id = id)
        val scraps = postScrapListCollector.get(id = id)

        return PostDetail(
            id = id,
            boardId = boardId,
            user = userCollector.get(id = userId),
            title = title,
            content = content,
            imagePaths = imagePaths,
            categories = postTagListCollector.get(id = id).content,
            likeCount = likes.content.size,
            commentCount = commentList.content.size,
            comments = commentList.content,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isLiked = likes.content.any { it.userId == callerId },
            isScrapped = scraps.content.any { it.userId == callerId },
            scrapCount = scraps.content.size,
        )
    }
}
