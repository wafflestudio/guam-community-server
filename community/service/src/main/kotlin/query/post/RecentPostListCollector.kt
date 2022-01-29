package waffle.guam.community.service.query.post

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.GuamCacheFactory
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.command.post.PostCreated
import waffle.guam.community.service.command.post.PostDeleted
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.query.Collector

@Service
class RecentPostListCollector(
    private val postListCollector: PostListCollector,
) : Collector<PostList, BoardId>, PostQueryGenerator {
    companion object {
        const val RECENT_POSTS_SIZE = 50
    }

    override fun get(id: BoardId): PostList =
        postListCollector.get(
            PostListCollector.Query(boardId = id, size = RECENT_POSTS_SIZE, afterPostId = 0L)
        )

    @Service
    class CacheImpl(
        private val impl: RecentPostListCollector,
        cacheFactory: GuamCacheFactory,
    ) : Collector<PostList, BoardId> {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = cacheFactory.getCache(
            name = "RECENT_POSTS_CACHE",
            loader = impl::get,
        )

        override fun get(id: BoardId): PostList = cache.get(id)

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

        // TODO: reload when post updated
    }
}
