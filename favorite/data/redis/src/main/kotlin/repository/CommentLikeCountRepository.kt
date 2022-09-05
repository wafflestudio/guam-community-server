package waffle.guam.favorite.data.redis.repository

import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import waffle.guam.favorite.data.redis.RedisConfig.Companion.COMMENT_LIKE_KEY

interface CommentLikeCountRepository {
    suspend fun gets(commentIds: List<Long>): Map<Long, Int>
    suspend fun increment(commentId: Long)
    suspend fun decrement(commentId: Long)
}

class CommentLikeCountRepositoryImpl(
    private val redis: ReactiveStringRedisTemplate,
) : CommentLikeCountRepository {

    override suspend fun gets(commentIds: List<Long>): Map<Long, Int> =
        redis.zGets(COMMENT_LIKE_KEY, commentIds)

    override suspend fun increment(commentId: Long) {
        redis.zInc(COMMENT_LIKE_KEY, commentId, 1.0)
    }

    override suspend fun decrement(commentId: Long) {
        redis.zInc(COMMENT_LIKE_KEY, commentId, -1.0)
    }
}
