package waffle.guam.community.service.command.notification

import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import waffle.guam.community.Log
import waffle.guam.community.service.command.immigration.ImmigrationClient

@Service
class PushNotifier(
    private val immigrationClient: ImmigrationClient,
) {
    companion object : Log

    @Async
    fun sendPush(userIds: List<Long>, title: String, body: String, imageUrl: String? = null) = runBlocking {
        immigrationClient.sendPush(userIds, title, body, imageUrl)
    }
}
