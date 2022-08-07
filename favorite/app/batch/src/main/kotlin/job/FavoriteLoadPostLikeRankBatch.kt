package waffle.guam.favorite.batch.job

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import waffle.guam.favorite.batch.model.PostLikeCount
import waffle.guam.favorite.batch.service.PostLikeBatchService
import javax.persistence.EntityManagerFactory

@Configuration
class FavoriteLoadPostLikeRankBatch(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val postLikeBatchService: PostLikeBatchService,
    private val entityManagerFactory: EntityManagerFactory,
) {
    @Bean(JOB_NAME)
    fun job() = jobBuilderFactory[JOB_NAME]
        .start(initStep())
        .next(loadStep())
        .preventRestart()
        .build()

    // TODO 쪼개야할까
    @Bean(JOB_NAME + "_initStep")
    @JobScope
    fun initStep() = stepBuilderFactory[JOB_NAME]
        .tasklet { _, _ ->
            postLikeBatchService.clearRank()
            RepeatStatus.FINISHED
        }
        .build()

    @Bean(JOB_NAME + "_step")
    @JobScope
    fun loadStep() = stepBuilderFactory[JOB_NAME]
        .chunk<PostLikeCount, PostLikeCount>(CHUNK_SIZE)
        .reader(reader())
        .writer(writer())
        .build()

    @Bean(JOB_NAME + "_reader")
    @StepScope
    fun reader() = JpaPagingItemReader<PostLikeCount>().apply {
        setEntityManagerFactory(entityManagerFactory)
        setQueryString(
            """
            select new waffle.guam.favorite.batch.model.PostLikeCount(postId, count(id)) 
            from PostLike group by postId
            """
        )
    }

    @Bean(JOB_NAME + "writer")
    @StepScope
    fun writer() = ItemWriter<PostLikeCount> { postLikeCounts ->
        postLikeBatchService.loadRank(postLikeCounts)
    }

    companion object {
        const val JOB_NAME = "FavoriteLoadPostLikeRankBatch"
        const val CHUNK_SIZE = 100
    }
}
