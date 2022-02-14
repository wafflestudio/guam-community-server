package waffle.guam.community.service.command.immigration

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import waffle.guam.immigration.api.user.SendPushRequest
import waffle.guam.immigration.client.user.UserGrpcClient

@Component
class ImmigrationClient(
    env: Environment,
) {
    private val client = when {
        "dev" in env.activeProfiles -> UserGrpcClient("dev")
        else -> TODO("Not yet implemented")
    }

    suspend fun sendPush(userIds: List<Long>, title: String, body: String, imageUrl: String? = null) {
        client.sendPush(SendPushRequest(userIds, title, body, imageUrl))
    }
}
