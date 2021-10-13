package waffle.guam.community.service.query.post

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.`in`
import waffle.guam.community.data.jdbc.eq
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostEntity_
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.post.PostCreated
import waffle.guam.community.service.command.post.PostDeleted
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class RecentPostListCollector(
    private val postRepository: PostRepository,
) : Collector<PostList, Long> {
    companion object {
        const val RECENT_POSTS_SIZE = 50
    }

    override fun get(id: Long): PostList {
        val spec = eq<PostEntity>(PostEntity_.BOARD_ID, id)
            .and(`in`<PostEntity>(PostEntity_.STATUS, listOf(PostEntity.Status.VALID)))

        val pageable = PageRequest.of(
            0,
            RECENT_POSTS_SIZE,
            Sort.by(Sort.Direction.DESC, PostEntity_.ID)
        )

        return postRepository.findAll(spec, pageable)
            .map { Post.of(it) }
            .let {
                PostList(
                    content = it.content,
                    hasNext = it.hasNext()
                )
            }
    }

    @Service
    class CacheImpl(
        private val impl: RecentPostListCollector,
    ) : Collector<PostList, Long> {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache(
            maximumSize = 10,
            duration = Duration.ofSeconds(10),
            loader = impl::get,
        )

        override fun get(id: Long): PostList = cache.get(id)

        @EventListener
        fun reload(postCreated: PostCreated) {
            cache.reload(postCreated.boardId)
            logger.info("Cache reloaded with $postCreated")
        }

        @EventListener
        fun reload(postDeleted: PostDeleted) {
            cache.reload(postDeleted.boardId)
            logger.info("Cache reloaded with $postDeleted")
        }
    }
}
