package waffle.guam.community.service.command.immigration

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import waffle.guam.immigration.api.push.req.PushSendRequest
import waffle.guam.immigration.client.DefaultImmigrationImpl

@Component
class ImmigrationClient(
    env: Environment,
) {
    private val client = when {
        "dev" in env.activeProfiles -> DefaultImmigrationImpl("dev")
        else -> DefaultImmigrationImpl("dev") // TODO("Not yet implemented")
    }

    suspend fun sendPush(userIds: List<Long>, title: String, body: String, imageUrl: String? = null) {
        client.pushService.sendPush(PushSendRequest(userIds, title, body, imageUrl, PushSendRequest.Source.COMMUNITY))
    }
}
