package waffle.guam.favorite.service.infra

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.favorite.service.ServiceProperties
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest

interface NotificationService {
    suspend fun notify(request: CreateNotificationRequest)

    data class CreateNotificationRequest(
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
}

@Service
class NotificationServiceImpl(
    private val community: CommunityService,
    properties: ServiceProperties,
    webClientBuilder: WebClient.Builder,
) : NotificationService {

    private val notification = webClientBuilder
        .baseUrl(properties.notification.url)
        .build()

    override suspend fun notify(request: CreateNotificationRequest) {
        notification.post()
            .uri("/api/v1/push/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .awaitBody<Unit>()
    }
}
