package waffle.guam.community.service.client

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.MultiCollector

@Service
class UserService(
    webClientBuilder: WebClient.Builder,
) : ClientService(webClientBuilder, BaseURL.USER), MultiCollector<User, UserId> {

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