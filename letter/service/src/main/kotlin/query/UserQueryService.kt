package waffle.guam.favorite.service.query

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.favorite.service.domain.User

interface UserQueryService {
    suspend fun get(userId: Long): User
    suspend fun get(userIds: List<Long>): Map<Long, User>
}

@Service
class UserQueryServiceImpl(
    webClientBuilder: WebClient.Builder,
) : UserQueryService {

    // FIXME: baseUrl 프로퍼티로 등록, 어느 패키지로 보낼까
    private val webClient = webClientBuilder.baseUrl("http://guam-user.jon-snow-korea.com")
        .build()

    override suspend fun get(userId: Long): User {
        return webClient.get()
            .uri("/api/v1/users/$userId")
            .retrieve()
            .awaitBody()
    }

    override suspend fun get(userIds: List<Long>): Map<Long, User> {
        return webClient.get()
            .uri("/api/v1/users?userIds={userIds}", userIds.joinToString(","))
            .retrieve()
            .awaitBody<List<User>>()
            .associateBy { it.id }
    }
}
