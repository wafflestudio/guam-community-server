package waffle.guam.letter.service.query

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.letter.service.domain.User

interface UserQueryService {
    suspend fun get(userId: Long): User
    suspend fun get(userIds: List<Long>): Map<Long, User>
}

@Service
class UserQueryServiceImpl(
    webClientBuilder: WebClient.Builder,
    userServiceProperties: UserServiceProperties,
) : UserQueryService {
    private val webClient = webClientBuilder.baseUrl(userServiceProperties.baseUrl).build()

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
