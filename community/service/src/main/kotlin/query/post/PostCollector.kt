package waffle.guam.community.service.query.post

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.GuamCacheFactory
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.command.post.PostCreated
import waffle.guam.community.service.command.post.PostDeleted
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.query.Collector

@Service
class PostCollector(
    private val postRepository: PostRepository,
) : Collector<Post, PostId> {
    override fun get(id: PostId): Post =
        postRepository.findById(id).orElseThrow { PostNotFound(id) }
            .let { Post(it) }

    @Service
    class CacheImpl(
        val impl: PostCollector,
        cacheFactory: GuamCacheFactory,
    ) : Collector<Post, PostId> {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = cacheFactory.getCache(
            name = "POST_CACHE",
            loader = impl::get,
        )

        override fun get(id: PostId): Post = cache.get(id)

        @EventListener
        fun reload(postCreated: PostCreated) {
            cache.reload(postCreated.postId)
            logger.info("Cache reloaded with $postCreated")
        }

        @EventListener
        fun reload(postDeleted: PostDeleted) {
            cache.invalidate(postDeleted.postId)
            logger.info("Cache invalidated with $postDeleted")
        }
    }
}
