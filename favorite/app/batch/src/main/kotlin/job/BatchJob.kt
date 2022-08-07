package waffle.guam.favorite.batch.job

import org.slf4j.LoggerFactory

/**
 * TODO Log Results?
 */
abstract class BatchJob<T> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun run(chunkSize: Long) {
        try {
            doRun(chunkSize)
        } catch (exception: Exception) {
            logger.info("JOB ABORTED with $exception.")
            logger.info("TRYING TO ROLLBACK DATA AS INITIAL STEP..")
            initStep()
            logger.info("ROLLBACK DONE")
        }
    }

    private fun doRun(chunkSize: Long) {
        initStep()
        var page = 0
        do {
            logger.info("STARTING PHASE $page")
            val result = doRead(page, chunkSize.toInt())
            result.writeToRedis()
            logger.info("FINISHED PHASE $page - WROTE $chunkSize DATA")
            page += 1
        } while (result.isNotEmpty())
        logger.info("SUCCESSFULLY FINISHED JOB.")
    }

    abstract fun initStep()

    abstract fun doRead(page: Int, pageSize: Int): List<T>

    abstract fun List<T>.writeToRedis()
}

object BatchJobNames {
    const val LOAD_POST_SCRAP_RANK = "LOAD_POST_SCRAP_RANK"
    const val LOAD_POST_LIKE_RANK = "LOAD_POST_LIKE_RANK"
    const val LOAD_POST_COMMENT_LIKE_RANK = "LOAD_POST_COMMENT_LIKE_RANK"
}
