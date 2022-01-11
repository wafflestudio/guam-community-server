package waffle.guam.community.slack

import com.slack.api.Slack
import com.slack.api.model.Attachment
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@EnableConfigurationProperties(SlackProperties::class)
@Component
class SlackUtils(
    private val slackProperties: SlackProperties,
) {
    @Value("\${spring.profiles.active:dev}")
    private val activeProfile = ""

    fun send(channel: SlackChannel, text: String, attachments: List<Attachment> = listOf()) {
        Slack.getInstance().methods(slackProperties.token).chatPostMessage { chatPostMessageRequestBuilder ->
            chatPostMessageRequestBuilder
                .channel(channel.chName)
                .text(text)
                .attachments(attachments)
        }
    }

    @PostConstruct
    @Profile("!local")
    fun sendApplicationRunning() {
        send(SlackChannel.DEPLOY, ">:white_check_mark: *$activeProfile 환경에서 서버가 작동 중입니다.*")
    }

    @PreDestroy
    @Profile("!local")
    fun sendApplicationStopping() {
        send(SlackChannel.DEPLOY, ">:exclamation: *$activeProfile 서버가 종료되었습니다.*")
    }
}

@ConstructorBinding
@ConfigurationProperties("slack.app")
data class SlackProperties(
    val token: String,
)

enum class SlackChannel(val chName: String) {
    DEPLOY("#team_guam_shout_for_deploy"),
    SERVER_TEAM("#team-괌_server"),
}
