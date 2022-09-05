package waffle.guam.favorite.batch.job

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.data.redis.core.addAllAndAwait
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.rangeWithScoresAsFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Component
import waffle.guam.favorite.batch.job.BatchJobNames.LOAD_POST_LIKE_RANK
import waffle.guam.favorite.data.redis.RedisConfig
import waffle.guam.favorite.service.infra.CommunityService

@Component(LOAD_POST_LIKE_RANK)
class FavoriteLoadPostLikeRankBatch(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val community: CommunityService,
    private val dbClient: DatabaseClient,
) : BatchJob<PostLikeCount>() {

    override fun initStep() = runBlocking {
        val redisKeys = listOf("", 1L, 2L, 3L, 4L, 5L).map { RedisConfig.POST_LIKE_KEY + it }
        redisTemplate.deleteAndAwait(*redisKeys.toTypedArray()); Unit
    }

    override fun doRead(lastId: Long, chunkSize: Int): Chunk<PostLikeCount> = runBlocking {
        dbClient
            .sql(
                """
                select post_id, count(id) as cnt
                from post_likes 
                where post_id > :lastId
                group by post_id
                order by post_id
                limit :chunkSize
                """.trimIndent()
            )
            .bind("lastId", lastId)
            .bind("chunkSize", chunkSize)
            .map { row ->
                PostLikeCount(
                    postId = (row.get("post_id") as Number).toLong(),
                    count = (row.get("cnt") as Number).toLong(),
                )
            }
            .flow()
            .toList()
            .let { result -> Chunk(result, result.lastOrNull()?.postId) }
    }

    override fun doWrite(result: List<PostLikeCount>) {
        insertAll(result)
        insertPerBoard(result)
    }

    private fun insertAll(data: List<PostLikeCount>) = runBlocking {
        data.ifEmpty { return@runBlocking }
            .associate { it.postId to it.count }
            .map { (postId, likeCount) -> ZSetOperations.TypedTuple.of("$postId", likeCount.toDouble()) }
            .apply { redisTemplate.opsForZSet().addAllAndAwait(RedisConfig.POST_LIKE_KEY, this.toSet()) }
    }

    private fun insertPerBoard(data: List<PostLikeCount>) = runBlocking {
        val postBoards = community
            .getPosts(postIds = data.map { it.postId })
            .mapValues { (_, post) -> post.boardId }

        redisTemplate.opsForZSet().rangeWithScoresAsFlow(RedisConfig.POST_LIKE_KEY, Range.closed(0, -1))
            .toList()
            .mapNotNull { tuple -> PostScore(tuple, postBoards) }
            .groupBy { postScore -> postScore.boardId }
            .forEach { (boardId, postScores) ->
                val key = RedisConfig.POST_LIKE_KEY + boardId
                val boardOps = postScores.map { ZSetOperations.TypedTuple.of("${it.postId}", it.score) }.toSet()
                redisTemplate.opsForZSet().addAllAndAwait(key, boardOps)
            }
    }

    private data class PostScore(
        val boardId: Long,
        val postId: Long,
        val score: Double,
    )

    private fun PostScore(tuple: ZSetOperations.TypedTuple<String>, postBoards: Map<Long, Long>): PostScore? {
        val postId = tuple.value!!.toLong()
        val boardId = postBoards[postId] ?: return null

        return PostScore(
            boardId = boardId,
            postId = postId,
            score = tuple.score!!,
        )
    }
}

data class PostLikeCount(
    val postId: Long,
    val count: Long
)
