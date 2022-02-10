package waffle.guam.community.service.command.notification

import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.user.UserRepository

@Service
class PushNotifier(
    private val userRepository: UserRepository,
) {
    companion object : Log

    @Async
    fun sendPush(userIds: List<Long>, title: String, body: String, imageUrl: String? = null) {
        // TODO Immigration으로 옮기기
//        val userTokens = userRepository.findAllById(userIds)
//            .mapNotNull { user -> user.deviceToken }
//            .ifEmpty { return@sendPush }
//
//        logOnError("FCM ERROR") {
//            log.info("Send message \"$title\" to $userIds")
//            sendFcmPush(userTokens, title, body, imageUrl)
//        }
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
