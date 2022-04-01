package waffle.guam.community.service.query.category

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.GuamCacheFactory
import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.command.post.PostUpdated
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.category.PostCategoryList
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class PostCategoryListCollector(
    private val postRepository: PostRepository,
) : MultiCollector<PostCategoryList, PostId>, PostQueryGenerator {
    override fun get(id: PostId): PostCategoryList =
        postRepository.findOne(spec = postId(id) * fetchCategories())
            ?.toPostCategoryList()
            ?: throw PostNotFound(id)

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostCategoryList> =
        postRepository.findAll(spec = postIds(ids) * fetchCategories())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .associate { post -> post.id to post.toPostCategoryList() }

    private fun PostEntity.toPostCategoryList() = PostCategoryList(
        postId = id,
        content = categories.map { it.toPostCategory() }
    )

    private fun PostCategoryEntity.toPostCategory() = PostCategory(
        postId = post.id,
        categoryId = category.id,
        title = category.title
    )

    @Service
    class CacheImpl(
        val impl: PostCategoryListCollector,
        cacheFactory: GuamCacheFactory,
    ) : MultiCollector<PostCategoryList, PostId> {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private val cache = cacheFactory.getCache(
            name = "POST_CATEGORIES_CACHE",
            ttl = Duration.ofMinutes(10),
            loader = impl::get,
            multiLoader = impl::multiGet
        )

        override fun get(id: PostId): PostCategoryList = cache.get(id)

        override fun multiGet(ids: Collection<PostId>): Map<PostId, PostCategoryList> = cache.multiGet(ids)

        @EventListener
        fun reload(event: PostUpdated) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }
    }
}
