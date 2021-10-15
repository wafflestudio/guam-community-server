package waffle.guam.community.service.query.like

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.like.PostLikeRepository
import waffle.guam.community.service.command.like.PostLikeCreated
import waffle.guam.community.service.command.like.PostLikeDeleted
import waffle.guam.community.service.domain.like.PostLike
import waffle.guam.community.service.domain.like.PostLikeList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class PostLikeListCollector(
    private val postLikeRepository: PostLikeRepository,
) : Collector<PostLikeList, Long> {
    override fun get(postId: Long): PostLikeList =
        PostLikeList(
            postId = postId,
            content = postLikeRepository.findAllByPostId(postId).map { PostLike.of(it) }
        )

    fun multiGet(postIds: Collection<Long>): Map<Long, PostLikeList> {
        val likeMap = postLikeRepository.findAllByPostIdIn(postIds)
            .groupBy { it.post.id }
            .mapValues { it.value.map { PostLike.of(it) } }

        return postIds.map { it to PostLikeList(postId = it, content = likeMap[it] ?: emptyList()) }.toMap()
    }

    @Service
    class CacheImpl(
        postLikeRepository: PostLikeRepository,
    ) : PostLikeListCollector(postLikeRepository) {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache<PostLikeList, Long>(
            maximumSize = 1000,
            duration = Duration.ofMinutes(1),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(postId: Long): PostLikeList = cache.get(postId)

        override fun multiGet(postIds: Collection<Long>): Map<Long, PostLikeList> = cache.multiGet(postIds)

        @EventListener
        fun reload(event: PostLikeCreated) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }

        @EventListener
        fun reload(event: PostLikeDeleted) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }
    }
}
