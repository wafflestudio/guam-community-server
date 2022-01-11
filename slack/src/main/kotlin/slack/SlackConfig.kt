package waffle.guam.community.slack

import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
@Profile("!local")
class SlackInitAlarmConfig(
    private val slackUtils: SlackUtils,
    env: Environment,
) {
    private val activeProfile = env.getProperty("spring.profiles.active", "dev")

    @PostConstruct
    fun sendApplicationRunning() {
        slackUtils.send(SlackChannel.DEPLOY, ">:white_check_mark: *$activeProfile 환경에서 서버가 작동 중입니다.*")
    }

    @PreDestroy
    fun sendApplicationStopping() {
        slackUtils.send(SlackChannel.DEPLOY, ">:exclamation: *$activeProfile 서버가 종료되었습니다.*")
    }
}
