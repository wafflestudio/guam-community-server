package waffle.guam.community.service.command

import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

abstract class CommandHandler<C : Command, R : Result> {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    @Transactional
    open fun handle(command: Command): Event<R, Exception> {
        if (!canHandle(command)) throw Exception()

        return try {
            Event.Result(internalHandle(command as C)).also { logger.info("${it.value}") }
        } catch (e: Exception) {
            Event.Error(e).also { logger.error("handler error", e) }
        }
    }

    abstract fun canHandle(command: Command): Boolean
    protected abstract fun internalHandle(command: C): R
}
