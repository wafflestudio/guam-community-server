package waffle.guam.favorite.service.query

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.reverseRangeAsFlow
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.r2dbc.repository.ScrapRepository
import waffle.guam.favorite.data.redis.RedisConfig.Companion.POST_SCRAP_KEY

interface ScrapCountStore {
    suspend fun getCount(postId: Long): Int
    suspend fun getCount(postIds: List<Long>): Map<Long, Int>

    interface Mutable : ScrapCountStore {
        suspend fun increment(postId: Long)
        suspend fun decrement(postId: Long)
    }

    interface Rank : ScrapCountStore {
        suspend fun getRank(from: Int, to: Int): List<Long>
    }
}

@Service
class ScrapCountStoreRedisImpl(
    private val scrapRepository: ScrapRepository,
    private val redisTemplate: ReactiveStringRedisTemplate,
) : ScrapCountStore, ScrapCountStore.Mutable, ScrapCountStore.Rank {

    override suspend fun getCount(postId: Long): Int {
        return redisTemplate.opsForZSet()
            .score(POST_SCRAP_KEY, "$postId")
            .awaitFirstOrNull()
            ?.toInt()
            ?: 0
    }

    override suspend fun getCount(postIds: List<Long>): Map<Long, Int> {
        if (postIds.isEmpty()) {
            return emptyMap()
        }

        val scores = redisTemplate.opsForZSet()
            .score(POST_SCRAP_KEY, *(postIds.map { "$it" }.toTypedArray()))
            .awaitSingle()

        return postIds.zip(scores)
            .map { it.first to (it.second?.toInt() ?: 0) }
            .toMap()
    }

    override suspend fun increment(postId: Long) {
        redisTemplate.opsForZSet()
            .incrementScore(POST_SCRAP_KEY, "$postId", 1.0)
            .awaitSingle()
    }

    override suspend fun decrement(postId: Long) {
        redisTemplate.opsForZSet()
            .incrementScore(POST_SCRAP_KEY, "$postId", -1.0)
            .awaitSingle()
    }

    override suspend fun getRank(from: Int, to: Int): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeAsFlow(POST_SCRAP_KEY, Range.closed(from.toLong(), to.toLong()))
            .map { it.toLong() }
            .toList()
    }
}
