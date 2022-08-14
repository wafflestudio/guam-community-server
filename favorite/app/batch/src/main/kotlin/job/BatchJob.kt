package waffle.guam.favorite.batch.job

import org.slf4j.LoggerFactory

abstract class BatchJob<T> {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private var lastId: Long? = 0L

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
        do {
            logger.info("FETCHING $lastId to ${chunkSize + lastId!!}")

            val (result, lastId) = doRead(lastId!!, chunkSize.toInt())
            doWrite(result)

            logger.info("FINISHED - WROTE $chunkSize DATA")
            this.lastId = lastId
        } while (result.isNotEmpty() && lastId != null)
        logger.info("SUCCESSFULLY FINISHED JOB.")
    }

    abstract fun initStep()

    abstract fun doRead(lastId: Long, chunkSize: Int): Chunk<T>

    abstract fun doWrite(result: List<T>)
}

data class Chunk<T>(
    val result: List<T>,
    val lastId: Long?,
)

object BatchJobNames {
    const val LOAD_POST_SCRAP_RANK = "LOAD_POST_SCRAP_RANK"
    const val LOAD_POST_LIKE_RANK = "LOAD_POST_LIKE_RANK"
    const val LOAD_POST_COMMENT_LIKE_RANK = "LOAD_POST_COMMENT_LIKE_RANK"
}
