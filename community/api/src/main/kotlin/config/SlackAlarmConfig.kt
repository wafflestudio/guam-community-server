package waffle.guam.community.config

import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import waffle.guam.community.event.ExceptionOccurred
import waffle.guam.community.slack.SlackChannel
import waffle.guam.community.slack.SlackUtils
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
@Profile("!local")
class SlackAlarmConfig(
    private val slackUtils: SlackUtils,
    env: Environment,
) {
    private val activeProfile = env.getProperty("spring.profiles.active", "dev")

    @Async
    @EventListener
    fun sendError(event: ExceptionOccurred) {
        slackUtils.sendErrorLog(event.exception)
    }

    @PostConstruct
    fun sendApplicationRunning() {
        slackUtils.send(SlackChannel.DEPLOY, ">:white_check_mark: *$activeProfile 환경에서 서버가 작동 중입니다.*")
    }

    @PreDestroy
    fun sendApplicationStopping() {
        slackUtils.send(SlackChannel.DEPLOY, ">:exclamation: *$activeProfile 서버가 종료되었습니다.*")
    }
}
