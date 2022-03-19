package waffle.guam.community

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Utils {
    companion object : Log
    // add any common POJO util logics here
}

interface Log {
    val log: Logger
        get() = LoggerFactory.getLogger(this.javaClass)
}

/**
 * Log If Error Occurs
 */
val Any?.isNull: Boolean
    get() = this == null

fun <T> logOnError(logger: Logger = Utils.log, msg: String, target: () -> T): Result<T> =
    runCatching {
        target.invoke()
    }.onFailure { exc ->
        logger.error("$msg: $exc")
    }

fun <T> logOnError(msg: String, target: () -> T): Result<T> =
    logOnError(Utils.log, msg, target)
