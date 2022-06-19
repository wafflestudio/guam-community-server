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
import waffle.guam.community.data.jdbc.post.fetchCategories
import waffle.guam.community.data.jdbc.post.fetchComments
import waffle.guam.community.data.jdbc.post.postIds
import waffle.guam.community.data.jdbc.post.status
import waffle.guam.community.data.jdbc.post.userId
import waffle.guam.community.data.jdbc.times
import waffle.guam.community.service.FavoriteService
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserService
import waffle.guam.community.service.domain.post.PostPreview
import waffle.guam.community.service.domain.post.PostPreviewList

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
        rankFrom: Int,
    ): PostPreviewList

    fun getSearchedPostPreview(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        before: PostId?,
    ): PostPreviewList

    fun getUserPostPreviews(
        userId: Long,
        before: PostId?,
    ): PostPreviewList

    fun getUserScrappedPostPreviews(
        userId: Long,
        before: PostId?,
    ): PostPreviewList
}

@Service
class PostPreviewServiceImpl(
    private val postRepository: PostRepository,
    private val favoriteService: FavoriteService,
    private val userService: UserService,
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

    override fun getFavoritePostPreviews(userId: Long, rankFrom: Int): PostPreviewList {
        val postIds = favoriteService.getRankedPosts(userId, rankFrom, rankFrom + PAGE_SIZE - 1)
            .let { PageImpl(it.take(PAGE_SIZE), PageRequest.of(0, PAGE_SIZE), it.size.toLong()) }

        return getCategoryAndComments(userId, postIds)
    }

    override fun getSearchedPostPreview(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        before: PostId?,
    ): PostPreviewList {
        fun searchFilter(categoryId: Long?, keyword: String): (PostEntity) -> Boolean = { post ->
            (post.title.contains(keyword) || post.content.contains(keyword)) &&
                (categoryId == null || post.categories.any { it.category.id == categoryId })
        }

        val spec = fetchCategories() * beforePostId(before) * status(PostEntity.Status.VALID)
        val postIds = postRepository.findAll(spec, SORT)
            .filter(searchFilter(categoryId, keyword))
            .map { it.id }
            .let { PageImpl(it.take(PAGE_SIZE), PageRequest.of(0, PAGE_SIZE), it.size.toLong()) }

        return getCategoryAndComments(userId, postIds)
    }

    override fun getUserPostPreviews(userId: Long, before: PostId?): PostPreviewList {
        val spec = beforePostId(before) * userId(userId) * status(PostEntity.Status.VALID)
        val postIds = postRepository.findAll(spec, PageRequest.of(0, PAGE_SIZE)).map { it.id }
        return getCategoryAndComments(userId, postIds)
    }

    override fun getUserScrappedPostPreviews(userId: Long, before: PostId?): PostPreviewList {
        TODO()
    }

    private fun getCategoryAndComments(userId: Long, postIds: Page<Long>): PostPreviewList = runBlocking {
        val posts = postRepository.findAll(spec = postIds(postIds.toList()) * fetchCategories() * fetchComments(), sort = SORT)
        val users = async { userService.multiGet(posts.map { it.userId }) }
        val favorites = async { favoriteService.getPostFavorite(userId, posts.map { it.id }) }

        posts
            .map { PostPreview(userId = userId, post = it, users = users.await(), favorites = favorites.await()) }
            .let { PostPreviewList(content = it, hasNext = postIds.hasNext()) }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private val SORT = Sort.by(Sort.Direction.DESC, "id")
    }
}
