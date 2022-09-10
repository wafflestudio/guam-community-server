package waffle.guam.favorite.data.redis.repository

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.reverseRangeAsFlow
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.redis.RedisConfig

interface PostLikeCountRepository {
    suspend fun get(postId: Long): Long
    suspend fun gets(postIds: List<Long>): Map<Long, Long>

    suspend fun increment(boardId: Long, postId: Long)
    suspend fun decrement(boardId: Long, postId: Long)

    suspend fun getRank(boardId: Long? = null, from: Long, to: Long): List<Long>
}

@Service
class PostLikeCountRepositoryImpl(
    private val redis: ReactiveStringRedisTemplate,
) : PostLikeCountRepository {

    private fun key(boardId: Long? = null) = if (boardId == null) {
        RedisConfig.POST_LIKE_KEY
    } else {
        "${RedisConfig.POST_LIKE_KEY}$boardId"
    }

    override suspend fun get(postId: Long): Long = redis.zGet(key(), postId)

    override suspend fun gets(postIds: List<Long>): Map<Long, Long> = redis.zGets(key(), postIds)

    override suspend fun increment(boardId: Long, postId: Long) {
        val newScore = redis.zInc(key(), postId, 1.0)

        redis.zAdd(key(boardId), postId, newScore)
    }

    override suspend fun decrement(boardId: Long, postId: Long) {
        val newScore = redis.zInc(key(), postId, -1.0)

        redis.zAdd(key(boardId), postId, newScore)
    }

    override suspend fun getRank(boardId: Long?, from: Long, to: Long): List<Long> =
        redis.opsForZSet().reverseRangeAsFlow(key(boardId), Range.closed(from, to))
            .map { it.toLong() }
            .toList()
}
