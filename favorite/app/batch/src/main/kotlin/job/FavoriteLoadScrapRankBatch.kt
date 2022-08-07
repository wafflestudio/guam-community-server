package waffle.guam.favorite.batch.job

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Component
import waffle.guam.favorite.batch.job.BatchJobNames.LOAD_POST_SCRAP_RANK
import waffle.guam.favorite.data.redis.RedisConfig

@Component(LOAD_POST_SCRAP_RANK)
class FavoriteLoadScrapRankBatch(
    private val redisTemplate: RedisTemplate<String, String>,
    private val dbClient: DatabaseClient,
) : BatchJob<PostScrapCount>() {

    override fun initStep() {
        redisTemplate.delete(RedisConfig.SCRAP_KEY)
    }

    override fun doRead(page: Int, pageSize: Int): List<PostScrapCount> = runBlocking {
        dbClient
            .sql(
                """
                select post_id, count(id) as cnt
                from post_scraps group by post_id
                limit :pageSize offset :offset
                """.trimIndent()
            )
            .bind("pageSize", pageSize)
            .bind("offset", page)
            .map { row ->
                PostScrapCount(
                    postId = (row.get("postId") as Number).toLong(),
                    count = (row.get("cnt") as Number).toLong(),
                )
            }
            .flow()
            .toList()
    }

    override fun List<PostScrapCount>.writeToRedis() {
        // insert all
        this.ifEmpty { return }
            .associate { it.postId to it.count }
            .map { ZSetOperations.TypedTuple.of("${it.key}", it.value.toDouble()) }
            .let { redisTemplate.opsForZSet().add(RedisConfig.SCRAP_KEY, it.toSet()) }
    }
}

data class PostScrapCount(
    val postId: Long,
    val count: Long
)
