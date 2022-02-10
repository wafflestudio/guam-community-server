package waffle.guam.immigration.server.user

import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.SendPushRequest
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.server.user.domain.User
import waffle.guam.immigration.server.user.domain.UserRepository

@Service
class UserServiceImpl(
    private val tokenHandler: FirebaseTokenHandler,
    private val userRepository: UserRepository,
) : UserService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun getUser(request: GetUserRequest): GetUserResponse =
        tokenHandler.getFirebaseUid(request.token)
            .let { userRepository.findByFirebaseUserId(it) ?: userRepository.save(User(firebaseUserId = it)) }
            .let { ApiUser(it) }
            .let { GetUserResponse(it) }

    override suspend fun sendPush(request: SendPushRequest) = CoroutineScope(Dispatchers.IO).launch {
        val deviceIds = mutableListOf<String>()

        userRepository.findAllById(request.userIds)
            .mapNotNull { user -> user.deviceId }
            .toList(deviceIds)

        sendFcmPush(deviceIds, request.title, request.body, request.imagePath)
    }.join()

    private suspend fun sendFcmPush(
        deviceIds: List<String>,
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
            .addAllTokens(deviceIds)
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
