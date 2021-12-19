package waffle.guam.community.service.query.post.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostAPIRepository
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
    private val postCommentListCollector: PostCommentListCollector.CacheImpl,
) {
    fun getPostPreviewList(boardId: Long, afterPostId: Long? = null): PostPreviewList =
        if (afterPostId == null) {
            // Cache for recent posts
            recentPostListCollector.get(boardId).fillData()
        } else {
            // No cache for old posts
            postListCollector.get(
                PostListCollector.Query(boardId = boardId, afterPostId = afterPostId, size = 20)
            ).fillData()
        }

    fun getSearchedPostPreviewList(
        boardId: Long,
        tag: String,
        keyword: String,
        afterPostId: Long? = null,
    ): PostPreviewList =
        // No cache for searched posts
        searchedPostListCollector.get(
            SearchedPostListCollector.Query(
                boardId = boardId,
                tag = tag,
                keyword = keyword,
                afterPostId = afterPostId ?: 0L,
                size = 20
            )
        ).fillData()

    fun getPostDetail(postId: Long): PostDetail =
        postCollector.get(postId).fillData()

    fun getUserPostList(
        userId: Long,
        afterPostId: Long?,
        sortByLikes: Boolean,
    ): List<MyPostView> {
        val data = postAPIRepository.findPostsOfUser(userId = userId, afterPostId = afterPostId, sortedByLikes = sortByLikes)
        return MyPostViewList(data)
    }

    private fun PostList.fillData(): PostPreviewList {
        // TODO: apply async
        val userMap = userCollector.multiGet(content.map { it.userId })
        val tagMap = postTagListCollector.multiGet(content.map { it.id })
        val likeMap = postLikeListCollector.multiGet(content.map { it.id })
        val commentMap = postCommentListCollector.multiGet(content.map { it.id })

        return PostPreviewList(
            content = content.map {
                PostPreview(
                    id = it.id,
                    boardId = it.boardId,
                    user = userMap[it.userId]!!,
                    title = it.title,
                    content = it.content,
                    isImageIncluded = it.isImageIncluded,
                    status = it.status,
                    tags = tagMap[it.id]?.content ?: emptyList(),
                    likeCount = likeMap[it.id]?.content?.size ?: 0,
                    commentCount = commentMap[it.id]?.content?.size ?: 0,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            },
            hasNext = hasNext
        )
    }

    private fun Post.fillData(): PostDetail {
        val commentList = postCommentListCollector.get(id = id)

        return PostDetail(
            id = id,
            boardId = boardId,
            user = userCollector.get(id = userId),
            title = title,
            content = content,
            imagePaths = imagePaths,
            tags = postTagListCollector.get(id = id).content,
            likeCount = postLikeListCollector.get(id = id).content.size,
            commentCount = commentList.content.size,
            comments = commentList.content,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
