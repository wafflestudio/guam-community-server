package waffle.guam.community.service.query.post

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.post.beforePostId
import waffle.guam.community.data.jdbc.post.boardId
import waffle.guam.community.data.jdbc.post.categoryIdMatching
import waffle.guam.community.data.jdbc.post.fetchCategories
import waffle.guam.community.data.jdbc.post.fetchComments
import waffle.guam.community.data.jdbc.post.fulltext
import waffle.guam.community.data.jdbc.post.postIds
import waffle.guam.community.data.jdbc.post.status
import waffle.guam.community.data.jdbc.post.userId
import waffle.guam.community.data.jdbc.times
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.post.AnonymousPostPreview
import waffle.guam.community.service.domain.post.PostPreview
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.favorite.api.model.PostFavoriteInfo
import waffle.guam.favorite.client.GuamFavoriteClient
import waffle.guam.user.client.GuamUserClient
import waffle.guam.user.domain.UserInfo

interface PostPreviewService {
    fun getRecentPreviews(
        userId: Long,
        boardId: Long? = null,
        before: PostId? = null,
    ): PostPreviewList

    fun getRecentPreviews(
        userId: Long,
        boardId: Long? = null,
        page: Int,
    ): PostPreviewList

    fun getFavoritePostPreviews(
        userId: Long,
        boardId: Long?,
        rankFrom: Int,
    ): PostPreviewList

    fun getSearchedPostPreview(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        before: PostId?,
    ): PostPreviewList

    fun getSearchResultCount(
        categoryId: Long?,
        keyword: String,
        before: PostId?,
    ): Long

    fun getUserPostPreviews(
        userId: Long,
        before: PostId?,
    ): PostPreviewList

    fun getUserScrappedPostPreviews(
        userId: Long,
        page: Int,
    ): PostPreviewList
}

@Service
class PostPreviewServiceImpl(
    private val postRepository: PostRepository,
    private val favoriteClient: GuamFavoriteClient.Blocking,
    private val userClient: GuamUserClient.Blocking,
) : PostPreviewService {

    override fun getRecentPreviews(userId: Long, boardId: Long?, before: PostId?): PostPreviewList {
        val spec = boardId(boardId) * status(PostEntity.Status.VALID) * beforePostId(before)
        val pageable = PageRequest.of(0, PAGE_SIZE, SORT)
        val postIds = postRepository.findAll(spec, pageable).map { it.id }

        return getCategoryAndComments(userId, postIds)
    }

    override fun getRecentPreviews(userId: Long, boardId: Long?, page: Int): PostPreviewList {
        val spec = boardId(boardId) * status(PostEntity.Status.VALID)
        val pageable = PageRequest.of(page, PAGE_SIZE, SORT)
        val postIds = postRepository.findAll(spec, pageable).map { it.id }

        return getCategoryAndComments(userId, postIds)
    }

    override fun getFavoritePostPreviews(userId: Long, boardId: Long?, rankFrom: Int): PostPreviewList {
        val postIds = favoriteClient.getRankedPostIds(boardId, rankFrom, rankFrom + PAGE_SIZE - 1)
        val postList = getCategoryAndComments(userId, postIds.toPage())
        val postMap = postList.content.associateBy { it.id }
        return postIds
            .mapNotNull { postId -> postMap[postId] }
            .let { posts -> PostPreviewList(posts, postList.hasNext) }
    }

    override fun getSearchedPostPreview(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        before: PostId?,
    ): PostPreviewList {
        val spec = categoryIdMatching(
            categoryId,
            fetchCategories = true
        ) * beforePostId(before) * status(PostEntity.Status.VALID) * fulltext(keyword)
        val postIds = postRepository.findAll(spec, SORT).map { it.id }

        return getCategoryAndComments(userId, postIds.toPage())
    }

    override fun getSearchResultCount(
        categoryId: Long?,
        keyword: String,
        before: PostId?,
    ): Long {
        val spec =
            categoryIdMatching(categoryId) * beforePostId(before) * status(PostEntity.Status.VALID) * fulltext(keyword)
        return postRepository.count(spec)
    }

    override fun getUserPostPreviews(userId: Long, before: PostId?): PostPreviewList {
        val spec = beforePostId(before) * userId(userId) * status(PostEntity.Status.VALID)
        val postIds = postRepository.findAll(spec, PageRequest.of(0, PAGE_SIZE, SORT)).map { it.id }
        return getCategoryAndComments(userId, postIds)
    }

    override fun getUserScrappedPostPreviews(userId: Long, page: Int): PostPreviewList {
        val postIds = favoriteClient.getScrappedPostIds(userId, page = page)
        return getCategoryAndComments(userId, postIds.toPage())
    }

    private fun getCategoryAndComments(userId: Long, postIds: Page<Long>): PostPreviewList = runBlocking {
        if (postIds.isEmpty) {
            return@runBlocking PostPreviewList(emptyList(), false)
        }

        val posts =
            postRepository.findAll(spec = postIds(postIds.toList()) * fetchCategories() * fetchComments(), sort = SORT)
        val users = async { userClient.getUsers(posts.filterNot { post -> post.isAnonymous }.map { it.userId }) }
        val favorites = async { favoriteClient.getPostInfos(userId, posts.map { it.id }) }

        posts
            .map { post -> post.toPreview(userId, users.await(), favorites.await()) }
            .let { PostPreviewList(content = it, hasNext = postIds.hasNext()) }
    }

    private fun PostEntity.toPreview(
        callerId: Long,
        users: Map<UserId, UserInfo>,
        favorites: Map<UserId, PostFavoriteInfo>,
    ): PostPreview = when (isAnonymous) {
        true -> AnonymousPostPreview(callerId, this, favorites[id]!!)
        false -> PostPreview(callerId, this, users[userId]!!, favorites[id]!!)
    }

    private fun List<PostId>.toPage(): Page<Long> {
        return PageImpl(take(PAGE_SIZE), PageRequest.of(0, PAGE_SIZE), size.toLong())
    }

    companion object {
        private const val PAGE_SIZE = 20
        private val SORT = Sort.by(Sort.Direction.DESC, "id")
    }
}
