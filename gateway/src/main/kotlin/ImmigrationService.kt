package waffle.guam.gateway

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

interface ImmigrationService {
    suspend fun getUser(token: String): ImmigrationUser?
}

data class ImmigrationUser(val userId: Long, val deviceId: String?)

@Service
class ImmigrationServiceImpl(
    webClientBuilder: WebClient.Builder,
) : ImmigrationService {

    // TODO: env에 따라 url 변경
    private val client: WebClient = webClientBuilder
        .baseUrl("http://guam-immigration.jon-snow-korea.com")
        .build()

    override suspend fun getUser(token: String): ImmigrationUser? = runCatching {
        client.get()
            .uri("/api/v1/auth?token={token}", token)
            .retrieve()
            .awaitBody<ImmigrationAuthResponse>()
            .user
    }.onFailure {
        if (it is WebClientResponseException && it.statusCode == HttpStatus.UNAUTHORIZED) {
            throw ImmigrationUnAuthorized()
        }
    }.getOrThrow()

    private data class ImmigrationAuthResponse(val user: ImmigrationUser? = null)

    class ImmigrationUnAuthorized : RuntimeException()
}
