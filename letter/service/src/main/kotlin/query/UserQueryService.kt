package waffle.guam.letter.service.query

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.letter.service.ServiceError
import waffle.guam.letter.service.domain.User
import waffle.guam.letter.service.query.UserQueryService.UserNotFound

interface UserQueryService {
    suspend fun get(userId: Long): User
    suspend fun get(userIds: List<Long>): Map<Long, User>

    class UserNotFound(
        override val status: Int = 404,
        override val msg: String = "유저를 찾을 수 없습니다."
    ) : ServiceError()
}

@Service
class UserQueryServiceImpl(
    webClientBuilder: WebClient.Builder,
    userServiceProperties: UserServiceProperties,
) : UserQueryService {
    private val webClient = webClientBuilder.baseUrl(userServiceProperties.baseUrl).build()

    override suspend fun get(userId: Long): User =
        webClient.get()
            .uri("/api/v1/users/$userId")
            .retrieve()
            .awaitBody<User>()
            .takeIf { it.status == User.Status.ACTIVE }
            ?: throw UserNotFound()

    override suspend fun get(userIds: List<Long>): Map<Long, User> =
        webClient.get()
            .uri("/api/v1/users?userIds={userIds}", userIds.joinToString(","))
            .retrieve()
            .awaitBody<List<User>>()
            .associateBy { it.id }
}
