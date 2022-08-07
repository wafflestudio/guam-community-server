package waffle.guam.favorite.batch.job

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@EnableConfigurationProperties(BatchJobProperty::class)
class BatchRunner(
    private val jobProperty: BatchJobProperty,
    private val batchJobs: Map<String, BatchJob<*>>
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        log.info("RUNNING BATCH $jobProperty..")
        log.info("REGISTERED JOBS: $batchJobs..")
    }

    @PostConstruct
    fun executeBatchJob() {
        batchJobs[jobProperty.names]
            ?.run(jobProperty.chunkSize)
            ?: throw IllegalArgumentException("${jobProperty.names} is not defined.")
    }
}

@ConstructorBinding
@ConfigurationProperties("spring.batch.job")
data class BatchJobProperty(
    val names: String,
    val chunkSize: Long,
)
