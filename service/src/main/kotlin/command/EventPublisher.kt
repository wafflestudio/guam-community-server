package waffle.guam.community.service.command

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Aspect
@Component
class EventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) {

    @Pointcut("within(waffle.guam..*Handler)")
    fun calledInService() {}

    @Around("calledInService()")
    fun doPublishEvent(jp: ProceedingJoinPoint): Any? {
        val event = jp.proceed()

        if (event is Event.Result<*> && event.value is Result) {
            eventPublisher.publishEvent(event.value)
        }

        return event
    }
}
