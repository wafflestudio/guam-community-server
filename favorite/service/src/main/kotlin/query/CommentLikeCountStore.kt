package waffle.guam.favorite.service.query

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.reverseRangeAsFlow
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.r2dbc.CommentLikeRepository
import waffle.guam.favorite.data.redis.RedisConfig.Companion.COMMENT_LIKE_KEY

interface CommentLikeCountStore {
    suspend fun getCount(commentId: Long): Int
    suspend fun getCount(commentIds: List<Long>): Map<Long, Int>

    interface Mutable : CommentLikeCountStore {
        suspend fun increment(commentId: Long)
        suspend fun decrement(commentId: Long)
    }

    interface Rank : CommentLikeCountStore {
        suspend fun getRank(from: Int, to: Int): List<Long>
    }
}

@Service
class CommentLikeCountStoreRedisImpl(
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val commentLikeRepository: CommentLikeRepository,
) : CommentLikeCountStore, CommentLikeCountStore.Mutable, CommentLikeCountStore.Rank {

    override suspend fun getCount(commentId: Long): Int {
        return redisTemplate.opsForZSet()
            .score(COMMENT_LIKE_KEY, "$commentId")
            .awaitFirstOrNull()
            ?.toInt()
            ?: 0
    }

    override suspend fun getCount(commentIds: List<Long>): Map<Long, Int> {
        if (commentIds.isEmpty()) {
            return emptyMap()
        }

        val scores = redisTemplate.opsForZSet()
            .score(COMMENT_LIKE_KEY, *(commentIds.map { "$it" }.toTypedArray()))
            .awaitSingle()

        return commentIds.zip(scores)
            .map { it.first to (it.second?.toInt() ?: 0) }
            .toMap()
    }

    override suspend fun increment(commentId: Long) {
        redisTemplate.opsForZSet()
            .incrementScore(COMMENT_LIKE_KEY, "$commentId", 1.0)
            .awaitSingle()
    }

    override suspend fun decrement(commentId: Long) {
        redisTemplate.opsForZSet()
            .incrementScore(COMMENT_LIKE_KEY, "$commentId", -1.0)
            .awaitSingle()
    }

    override suspend fun getRank(from: Int, to: Int): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeAsFlow(COMMENT_LIKE_KEY, Range.closed(from.toLong(), to.toLong()))
            .map { it.toLong() }
            .toList()
    }
}
