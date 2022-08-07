package waffle.guam.favorite.batch.service

import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations.TypedTuple
import org.springframework.stereotype.Service
import waffle.guam.favorite.batch.model.PostLikeCount
import waffle.guam.favorite.data.redis.RedisConfig.Companion.LIKE_KEY
import waffle.guam.favorite.service.infra.CommunityService

interface PostLikeBatchService {
    fun clearRank()
    fun loadRank(data: List<PostLikeCount>)
}

@Service
class PostLikeBatchServiceImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val community: CommunityService,
) : PostLikeBatchService {
    override fun clearRank() {
        listOf(null, 1L, 2L, 3L, 4L, 5L).forEach { redisTemplate.delete(LIKE_KEY + it) }
    }

    override fun loadRank(data: List<PostLikeCount>) {
        insertAll(data)
        insertPerBoard(data)
    }

    private fun insertAll(data: List<PostLikeCount>) {
        data
            .associate { it.postId to it.count }
            .map { (postId, likeCount) -> TypedTuple.of("$postId", likeCount.toDouble()) }
            .apply { redisTemplate.opsForZSet().add(LIKE_KEY, this.toSet()) }
    }

    private fun insertPerBoard(data: List<PostLikeCount>) {
        val postBoards = runBlocking {
            community
                .getPosts(postIds = data.map { it.postId })
                .mapValues { (_, post) -> post.boardId }
        }

        redisTemplate.opsForZSet().rangeWithScores(LIKE_KEY, 0, -1)!!
            .toList()
            .mapNotNull { tuple -> PostScore(tuple, postBoards) }
            .groupBy { postScore -> postScore.boardId }
            .forEach { (boardId, postScores) ->
                val key = LIKE_KEY + boardId
                val boardOps = postScores.map { TypedTuple.of("${it.postId}", it.score) }.toSet()
                redisTemplate.opsForZSet().add(key, boardOps)
            }
    }

    private data class PostScore(
        val boardId: Long,
        val postId: Long,
        val score: Double,
    )

    private fun PostScore(tuple: TypedTuple<String>, postBoards: Map<Long, Long>): PostScore? {
        val postId = tuple.value!!.toLong()
        val boardId = postBoards[postId] ?: return null

        return PostScore(
            boardId = boardId,
            postId = postId,
            score = tuple.score!!,
        )
    }
}
