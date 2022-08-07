package waffle.guam.favorite.batch.job

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Component
import waffle.guam.favorite.batch.job.BatchJobNames.LOAD_POST_COMMENT_LIKE_RANK
import waffle.guam.favorite.data.redis.RedisConfig

@Component(LOAD_POST_COMMENT_LIKE_RANK)
class FavoriteLoadCommentLikeRankBatch(
    private val redisTemplate: RedisTemplate<String, String>,
    private val dbClient: DatabaseClient
) : BatchJob<PostCommentLikeCount>() {

    override fun initStep() {
        redisTemplate.delete(RedisConfig.COMMENT_LIKE_KEY)
    }

    override fun doRead(page: Int, pageSize: Int): List<PostCommentLikeCount> = runBlocking {
        dbClient
            .sql(
                """
                select post_comment_id, count(id) as cnt
                from post_comment_likes group by post_comment_id
                limit :pageSize offset :offset
                """.trimIndent()
            )
            .bind("pageSize", pageSize)
            .bind("offset", page)
            .map { row ->
                PostCommentLikeCount(
                    postCommentId = (row.get("post_comment_id") as Number).toLong(),
                    count = (row.get("cnt") as Number).toLong(),
                )
            }
            .flow()
            .toList()
    }

    override fun List<PostCommentLikeCount>.writeToRedis() {
        this.ifEmpty { return }
            .associate { it.postCommentId to it.count }
            .map { ZSetOperations.TypedTuple.of("${it.key}", it.value.toDouble()) }
            .let { redisTemplate.opsForZSet().add(RedisConfig.COMMENT_LIKE_KEY, it.toSet()) }
    }
}

data class PostCommentLikeCount(
    val postCommentId: Long,
    val count: Long,
)
