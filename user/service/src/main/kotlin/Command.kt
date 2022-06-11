package waffle.guam.user.service

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface Command
interface CommandService

@Aspect
@Component
class CommandLogger {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Before("within(waffle.guam..*CommandService+)")
    fun doPublishEvent(jp: JoinPoint) {
        logger.info("[${jp.target.javaClass.simpleName}][${jp.signature.name}] : (${jp.args.joinToString(",")})")
    }
}
