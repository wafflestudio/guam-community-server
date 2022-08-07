package waffle.guam.favorite.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

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
annotation class BatchTest
