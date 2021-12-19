package waffle.guam.community.service.query.post

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.command.post.PostCreated
import waffle.guam.community.service.command.post.PostDeleted
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class PostCollector(
    private val postRepository: PostRepository,
) : Collector<Post, PostId> {
    override fun get(id: PostId): Post =
        postRepository.findById(id).orElseThrow { Exception("POST NOT FOUND $id") }
            .let { Post(it) }

    @Service
    class CacheImpl(
        val impl: PostCollector
    ) : Collector<Post, PostId> {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache(
            maximumSize = 1000,
            duration = Duration.ofMinutes(1),
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
