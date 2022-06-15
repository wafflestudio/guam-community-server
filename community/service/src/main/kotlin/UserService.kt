package waffle.guam.community.service

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.MultiCollector

@Service
class UserService(
    webClientBuilder: WebClient.Builder,
) : MultiCollector<User, UserId> {

    // FIXME: baseUrl 프로퍼티로 등록, 어느 패키지로 보낼까
    private val webClient = webClientBuilder.baseUrl("http://guam-user.jon-snow-korea.com")
        .build()

    override fun get(id: UserId): User = runBlocking {
        webClient.get()
            .uri("/api/v1/users/$id")
            .retrieve()
            .awaitBody()
    }

    override fun multiGet(ids: Collection<UserId>): Map<UserId, User> = runBlocking {
        webClient.get()
            .uri("/api/v1/users?userIds={userIds}", ids.joinToString(","))
            .retrieve()
            .awaitBody<List<User>>()
            .associateBy { it.id }
    }
}
