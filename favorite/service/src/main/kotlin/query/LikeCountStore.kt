package waffle.guam.favorite.service.query

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.data.redis.core.reverseRangeAsFlow
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.r2dbc.LikeRepository
import waffle.guam.favorite.data.redis.RedisConfig.Companion.LIKE_KEY
import waffle.guam.favorite.service.infra.CommunityService

interface LikeCountStore {
    suspend fun getCount(postId: Long): Int
    suspend fun getCount(postIds: List<Long>): Map<Long, Int>

    interface Mutable : LikeCountStore {
        suspend fun increment(boardId: Long, postId: Long, delta: Double)
    }

    interface Rank : LikeCountStore {
        suspend fun getRank(boardId: Long? = null, from: Int, to: Int): List<Long>
        suspend fun loadRank()
    }
}

@Service
class LikeCountStoreRedisImpl(
    private val likeRepository: LikeRepository,
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val community: CommunityService,
) : LikeCountStore, LikeCountStore.Mutable, LikeCountStore.Rank {

    private fun key(boardId: Long? = null) = if (boardId == null) {
        LIKE_KEY
    } else {
        "$LIKE_KEY$boardId"
    }

    override suspend fun getCount(postId: Long): Int {
        return redisTemplate.opsForZSet()
            .score(key(), "$postId")
            .awaitFirstOrNull()
            ?.toInt()
            ?: 0
    }

    override suspend fun getCount(postIds: List<Long>): Map<Long, Int> {
        if (postIds.isEmpty()) {
            return emptyMap()
        }

        val scores = redisTemplate.opsForZSet()
            .score(key(), *(postIds.map { "$it" }.toTypedArray()))
            .awaitSingle()

        return postIds.zip(scores)
            .map { it.first to (it.second?.toInt() ?: 0) }
            .toMap()
    }

    override suspend fun increment(boardId: Long, postId: Long, delta: Double) {
        redisTemplate.opsForZSet()
            .incrementScore(key(), "$postId", delta)
            .map { redisTemplate.opsForZSet().add(key(boardId), "$postId", it) }
            .awaitSingle()
    }

    override suspend fun getRank(boardId: Long?, from: Int, to: Int): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeAsFlow(key(boardId), Range.closed(from.toLong(), to.toLong()))
            .map { it.toLong() }
            .toList()
    }

    override suspend fun loadRank() {
        // clear all
        listOf(null, 1L, 2L, 3L, 4L, 5L).forEach { redisTemplate.delete(key(it)).awaitSingle() }

        // get info
        val postLikes = likeRepository.findAll()
            .toList()
            .groupBy { it.postId }
            .map { it.key to it.value.size }
        val postBoard = community.getPosts(postLikes.map { it.first }).mapValues { (_, post) -> post.boardId }

        // insert all
        val allOps = postLikes.map { (postId, likeCount) ->
            ZSetOperations.TypedTuple.of("$postId", likeCount.toDouble())
        }
        redisTemplate.opsForZSet().addAll(key(), allOps).awaitSingle()

        // insert per boardId
        redisTemplate.opsForZSet().rangeWithScores(key(), Range.closed(0L, -1L))
            .asFlow()
            .toList()
            .map { it.value!!.toLong() to it.score!! }
            .filter { postBoard[it.first] != null }
            .groupBy { postBoard[it.first]!! }
            .forEach { (boardId, postLikes) ->
                val key = key(boardId)
                val boardOps = postLikes.map { ZSetOperations.TypedTuple.of("${it.first}", it.second) }
                redisTemplate.opsForZSet().addAll(key, boardOps).awaitSingle()
            }
    }
}
