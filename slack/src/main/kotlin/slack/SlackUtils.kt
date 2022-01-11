package waffle.guam.community.slack

import com.slack.api.Slack
import com.slack.api.model.Attachment
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@EnableConfigurationProperties(SlackProperties::class)
@Component
class SlackUtils(
    private val slackProperties: SlackProperties,
) {
    fun send(channel: SlackChannel, text: String, attachments: List<Attachment> = listOf()) {
        Slack.getInstance().methods(slackProperties.token).chatPostMessage { chatPostMessageRequestBuilder ->
            chatPostMessageRequestBuilder
                .channel(channel.chName)
                .text(text)
                .attachments(attachments)
        }
    }
}

@ConstructorBinding
@ConfigurationProperties("slack.app")
data class SlackProperties(
    val token: String,
)

enum class SlackChannel(val chName: String) {
    DEPLOY("#team-괌_guam"),
    SERVER_TEAM("#team-괌_server"),
}
