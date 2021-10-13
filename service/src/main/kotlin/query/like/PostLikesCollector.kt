package waffle.guam.community.service.query.like

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.like.PostLikeRepository
import waffle.guam.community.service.domain.like.PostLikes
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class PostLikesCollector(
    private val postLikeRepository: PostLikeRepository,
) : Collector<PostLikes, Long> {
    override fun get(postId: Long): PostLikes =
        PostLikes(
            postId = postId,
            userIds = postLikeRepository.findAllByPostId(postId).map { it.userId }
        )

    fun multiGet(postIds: Collection<Long>): Map<Long, PostLikes> {
        val likeMap = postLikeRepository.findAllByPostIdIn(postIds)
            .groupBy { it.postId }
            .mapValues { it.value.map { it.userId } }

        return postIds.map { it to PostLikes(postId = it, userIds = likeMap[it] ?: emptyList()) }.toMap()
    }

    @Service
    class CacheImpl(
        postLikeRepository: PostLikeRepository,
    ) : PostLikesCollector(postLikeRepository) {
        private val cache = Cache<PostLikes, Long>(
            maximumSize = 500,
            duration = Duration.ofMinutes(1),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(postId: Long): PostLikes = cache.get(postId)

        override fun multiGet(postIds: Collection<Long>): Map<Long, PostLikes> = cache.multiGet(postIds)
    }
}
