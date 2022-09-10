package waffle.guam.favorite.batch.job

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.data.redis.core.addAllAndAwait
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Component
import waffle.guam.favorite.batch.job.BatchJobNames.LOAD_POST_SCRAP_RANK
import waffle.guam.favorite.data.redis.RedisConfig

@Component(LOAD_POST_SCRAP_RANK)
class FavoriteLoadScrapRankBatch(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val dbClient: DatabaseClient,
) : BatchJob<PostScrapCount>() {

    override fun initStep() = runBlocking {
        redisTemplate.deleteAndAwait(RedisConfig.POST_SCRAP_KEY); Unit
    }

    override fun doRead(lastId: Long, chunkSize: Int): Chunk<PostScrapCount> = runBlocking {
        dbClient
            .sql(
                """
                select post_id, count(id) as cnt
                from post_scraps 
                where post_id > :lastId
                group by post_id
                order by post_id
                limit :chunkSize
                """.trimIndent()
            )
            .bind("chunkSize", chunkSize)
            .bind("lastId", lastId)
            .map { row ->
                PostScrapCount(
                    postId = (row.get("post_id") as Number).toLong(),
                    count = (row.get("cnt") as Number).toLong(),
                )
            }
            .flow()
            .toList()
            .let { result -> Chunk(result, result.lastOrNull()?.postId) }
    }

    // insert all
    override fun doWrite(result: List<PostScrapCount>) = runBlocking {
        result.ifEmpty { return@runBlocking }
            .associate { it.postId to it.count }
            .map { ZSetOperations.TypedTuple.of("${it.key}", it.value.toDouble()) }
            .apply { redisTemplate.opsForZSet().addAllAndAwait(RedisConfig.POST_SCRAP_KEY, this.toSet()) }
    }
}

data class PostScrapCount(
    val postId: Long,
    val count: Long
)
