package waffle.guam.community.slack

import com.slack.api.Slack
import com.slack.api.model.Attachment
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

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

    fun sendFile(
        channels: List<SlackChannel>,
        filename: String,
        content: String,
        title: String? = null,
        initialComment: String? = null,
        fileType: String = "text",
    ) {
        Slack.getInstance().methods(slackProperties.token).filesUpload { filesUploadRequestBuilder ->
            filesUploadRequestBuilder
                .channels(channels.map { it.chName })
                .filetype(fileType)
                .filename(filename)
                .content(content)
                .title(title ?: filename)
                .initialComment(initialComment ?: filename)
        }
    }

    fun sendErrorLog(exception: Throwable) {
        val stacktrace =
            StringWriter()
                .apply { exception.printStackTrace(PrintWriter(this)) }
                .toString()

        sendFile(
            channels = listOf(SlackChannel.ERROR),
            filename = "log-${Instant.now()}.txt",
            content = stacktrace,
            fileType = "text",
            initialComment = ":exclamation: Exception 발생: *${exception::class}*"
        )
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
    ERROR("#team-괌_guam-log")
}
