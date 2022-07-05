package waffle.guam.community.service.client

import org.springframework.web.reactive.function.client.WebClient

abstract class ClientService(
    webClientBuilder: WebClient.Builder,
    baseUrl: BaseURL,
) {
    protected val webClient =
        webClientBuilder.baseUrl(baseUrl.value).build()
}

enum class BaseURL(val value: String) {
    USER("http://guam-user.jon-snow-korea.com"),
    FAVORITE("http://guam-favorite.jon-snow-korea.com");
}
