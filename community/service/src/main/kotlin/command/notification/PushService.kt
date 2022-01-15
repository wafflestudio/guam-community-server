package waffle.guam.community.service.command.notification

import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.logOnError

@Service
class PushService(
    private val userRepository: UserRepository,
) {
    companion object : Log

    fun sendPush(userIds: List<Long>, title: String, body: String) {
        val userTokens = userRepository.findAllById(userIds)
            .mapNotNull { user -> user.deviceToken }
            .ifEmpty { return@sendPush }

        logOnError("FCM ERROR") {
            log.info("Send message \"$title\" to $userIds")
            sendFcmPush(userTokens, title, body)
        }
    }

    private fun sendFcmPush(
        userTokens: List<String>,
        title: String,
        body: String,
        imageUrl: String? = null,
    ) {
        val push = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .setImage(imageUrl)
            .build()
        val msg = MulticastMessage.builder()
            .addAllTokens(userTokens)
            .setNotification(push)
            .build()

        FirebaseMessaging.getInstance()
            .sendMulticast(msg)
            .logOnFailure()
    }

    private fun BatchResponse.logOnFailure() {
        responses
            .filterNot { it.isSuccessful }
            .forEach { log.error("FCM FAILURE: $it") }
    }
}
