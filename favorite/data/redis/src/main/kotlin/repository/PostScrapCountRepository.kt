package waffle.guam.favorite.data.redis.repository

import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.redis.RedisConfig.Companion.POST_SCRAP_KEY

interface PostScrapCountRepository {
    suspend fun gets(postIds: List<Long>): Map<Long, Long>
    suspend fun increment(postId: Long)
    suspend fun decrement(postId: Long)
}

@Service
class PostScrapCountRepositoryImpl(
    private val redis: ReactiveStringRedisTemplate,
) : PostScrapCountRepository {

    override suspend fun gets(postIds: List<Long>): Map<Long, Long> =
        redis.zGets(POST_SCRAP_KEY, postIds)

    override suspend fun increment(postId: Long) {
        redis.zInc(POST_SCRAP_KEY, postId, 1.0)
    }

    override suspend fun decrement(postId: Long) {
        redis.zInc(POST_SCRAP_KEY, postId, -1.0)
    }
}
