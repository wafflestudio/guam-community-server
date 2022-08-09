package waffle.guam.community.config

import com.slack.api.Slack
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import java.time.Instant

@EnableAsync
@Configuration
@EnableConfigurationProperties(SlackProperties::class)
class SlackAlertConfig(
    val props: SlackProperties,
) {
    @Async
    @EventListener
    fun handle(event: ExceptionHappened) {
        Slack.getInstance()
            .methods(props.token)
            .filesUpload { filesUploadRequestBuilder ->
                filesUploadRequestBuilder
                    .channels(listOf(props.channel))
                    .filetype("text")
                    .filename("log-${Instant.now()}.txt")
                    .content(event.throwable.stackTraceToString())
                    .initialComment(":exclamation: Exception 발생: *${event.throwable::class.simpleName}*")
            }
    }

}

@ConstructorBinding
@ConfigurationProperties("slack.app")
data class SlackProperties(
    val channel: String,
    val token: String,
)
