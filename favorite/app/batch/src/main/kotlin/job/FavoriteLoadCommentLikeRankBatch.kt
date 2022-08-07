package waffle.guam.favorite.batch.job

import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import waffle.guam.favorite.batch.model.PostCommentLikeCount
import waffle.guam.favorite.data.redis.RedisConfig
import javax.persistence.EntityManagerFactory

@Configuration
class FavoriteLoadCommentLikeRankBatch(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Bean(JOB_NAME)
    fun job() = jobBuilderFactory[JOB_NAME]
        .start(loadStep())
        .preventRestart()
        .build()

    @Bean(JOB_NAME + "_initStep")
    @JobScope
    fun initStep(): Step = stepBuilderFactory[JOB_NAME]
        .tasklet { _, _ ->
            redisTemplate.delete(RedisConfig.COMMENT_LIKE_KEY)
            RepeatStatus.FINISHED
        }
        .build()

    @Bean(JOB_NAME + "_step")
    @JobScope
    fun loadStep(): Step = stepBuilderFactory[JOB_NAME]
        .chunk<PostCommentLikeCount, PostCommentLikeCount>(CHUNK_SIZE)
        .reader(reader())
        .writer(writer())
        .build()

    @Bean(JOB_NAME + "_reader")
    @StepScope
    fun reader() = JpaPagingItemReader<PostCommentLikeCount>().apply {
        setEntityManagerFactory(entityManagerFactory)
        setQueryString(
            """
            select new waffle.guam.favorite.batch.model.PostCommentLikeCount(postCommentId, count(id)) 
            from PostCommentLike group by postCommentId
            """
        )
    }

    @Bean(JOB_NAME + "writer")
    @StepScope
    fun writer() = ItemWriter<PostCommentLikeCount> { postCommentLikeCounts ->
        postCommentLikeCounts
            .associate { it.postCommentId to it.count }
            .map { ZSetOperations.TypedTuple.of("${it.key}", it.value.toDouble()) }
            .let { redisTemplate.opsForZSet().add(RedisConfig.COMMENT_LIKE_KEY, it.toSet()) }
    }

    companion object {
        const val JOB_NAME = "FavoriteLoadCommentLikeRankBatch"
        const val CHUNK_SIZE = 100
    }
}
