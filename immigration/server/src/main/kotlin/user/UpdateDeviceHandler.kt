package waffle.guam.immigration.server.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.immigration.server.exception.UserNotFound
import waffle.guam.immigration.server.user.domain.UserRepository

@Service
class UpdateDeviceHandler(
    private val userRepository: UserRepository,
) {
    @Transactional
    suspend fun handle(userId: Long, deviceId: String) {
        val user = userRepository.findById(userId) ?: throw UserNotFound()
        user.deviceId = deviceId
    }
}
