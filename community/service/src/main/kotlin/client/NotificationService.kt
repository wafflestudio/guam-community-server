package waffle.guam.community.service.client

import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

interface NotificationService {
    fun notify(request: NotificationRequest)
}

@Service
@EnableConfigurationProperties(NotificationServiceProperties::class)
class NotificationServiceImpl(
    webClientBuilder: WebClient.Builder,
    notificationServiceProperties: NotificationServiceProperties,
) : NotificationService {
    private val notification = webClientBuilder
        .baseUrl(notificationServiceProperties.baseUrl)
        .build()

    override fun notify(request: NotificationRequest) = runBlocking {
        notification.post()
            .uri("/api/v1/push/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .awaitBody<Unit>()
    }
}

data class NotificationRequest(
    val producerId: Long,
    val infos: List<Info>,
) {
    data class Info(
        val consumerId: Long,
        val kind: String,
        val body: String,
        val linkUrl: String,
        val isAnonymousEvent: Boolean,
    )
}

@ConstructorBinding
@ConfigurationProperties("guam.services.notification")
data class NotificationServiceProperties(
    val baseUrl: String = ""
)
