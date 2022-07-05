package waffle.guam.community.service.client

import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.user.User

interface UserService {
    fun get(id: UserId): User

    fun multiGet(ids: Collection<UserId>): Map<UserId, User>
}

@Service
@EnableConfigurationProperties(UserServiceProperties::class)
class UserServiceImpl(
    webClientBuilder: WebClient.Builder,
    userServiceProperties: UserServiceProperties,
) : UserService {

    private val webClient = webClientBuilder.baseUrl(userServiceProperties.baseUrl).build()
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

@ConstructorBinding
@ConfigurationProperties("guam.services.user")
data class UserServiceProperties(
    val baseUrl: String = ""
)
