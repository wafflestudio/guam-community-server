package waffle.guam.immigration.server.user

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.SendPushRequest
import waffle.guam.immigration.api.user.UpdateUserDeviceRequest
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.server.exception.UserNotFound
import waffle.guam.immigration.server.user.domain.User
import waffle.guam.immigration.server.user.domain.UserRepository

@Service
class UserServiceImpl(
    private val tokenHandler: FirebaseTokenHandler,
    private val userRepository: UserRepository,
) : UserService {
    override suspend fun getUser(request: GetUserRequest): GetUserResponse =
        tokenHandler.getFirebaseUid(request.token)
            .let { userRepository.findByFirebaseUserId(it) ?: userRepository.save(User(firebaseUserId = it)) }
            .let { ApiUser(it) }
            .let { GetUserResponse(it) }

    @Transactional
    override suspend fun updateUserDevice(request: UpdateUserDeviceRequest) =
        tokenHandler.getFirebaseUid(request.token)
            .let { userRepository.findByFirebaseUserId(it) ?: throw UserNotFound() }
            .run { firebaseDeviceId = request.deviceId }

    override suspend fun sendPush(request: SendPushRequest) =
        userRepository.findAllById(request.userIds)
            .mapNotNull { user -> user.firebaseDeviceId }
            .toList()
            .let { sendFcmPush(it, request.title, request.body, request.imagePath) }

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
            .sendMulticastAsync(msg)
    }
}
