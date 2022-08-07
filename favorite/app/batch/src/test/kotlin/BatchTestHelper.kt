package waffle.guam.favorite.batch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import waffle.guam.favorite.service.infra.Comment
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.Post

@Component
class BatchTestHelper(
    private val applicationContext: ApplicationContext,
    private val jobLauncher: JobLauncher,
    private val jobRepository: JobRepository,
) {
    fun launch(jobName: String): JobExecution {
        return JobLauncherTestUtils().apply {
            jobLauncher = this@BatchTestHelper.jobLauncher
            jobRepository = this@BatchTestHelper.jobRepository
            job = applicationContext.getBean(jobName, Job::class.java)
        }.launchJob()
    }
}

@SpringBootTest("spring.cloud.vault.enabled=false")
annotation class BatchTest {
    @Primary
    @Service
    class TestCommunity : CommunityService {

        private val boardIdMap: MutableMap<Long, Long> = mutableMapOf()

        private val basePost =
            Post(id = 0, boardId = 1, userId = 0, title = "", content = "", status = "", isAnonymous = false)

        fun setBoardIdOfPostForNextCall(vararg postBoardIdPairs: Pair<Long, Long>) {
            boardIdMap.clear()
            boardIdMap.putAll(postBoardIdPairs.toMap())
        }

        override suspend fun getPosts(postIds: List<Long>): Map<Long, Post> {
            return postIds
                .map { basePost.copy(id = it, boardId = boardIdMap[it] ?: 0L) }
                .associateBy { it.id }
        }

        override suspend fun getComment(commentId: Long): Comment? = TODO()
        override suspend fun getPost(postId: Long): Post? = TODO()

        @AfterEach
        fun destroy() {
            boardIdMap.clear()
        }
    }
}

fun JobExecution.shouldComplete() =
    assertThat(status).isEqualTo(BatchStatus.COMPLETED)
