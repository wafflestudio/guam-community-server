package waffle.guam.community.config

import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import waffle.guam.community.slack.SlackUtils

@Component
@Profile("!local")
class SlackAlarmConfig(
    private val slackUtils: SlackUtils,
) {
    @Async
    @EventListener
    fun sendError(event: ExceptionHandler.UnHandledException) {
        slackUtils.sendErrorLog(event.exc)
    }
}
