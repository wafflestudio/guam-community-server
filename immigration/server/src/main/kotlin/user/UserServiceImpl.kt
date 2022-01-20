package waffle.guam.immigration.server.user

import org.springframework.stereotype.Service
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.server.FirebaseTokenVerifier
import waffle.guam.immigration.server.user.domain.UserRepository

@Service
class UserServiceImpl(
    private val tokenVerifier: FirebaseTokenVerifier,
    private val userRepository: UserRepository,
) : UserService {
    override suspend fun getUser(request: GetUserRequest): GetUserResponse =
        tokenVerifier.getFirebaseUid(request.token)
            .let { userRepository.findByFUid(it) }
            ?.let { ApiUser(it) }
            .let { GetUserResponse(it) }
}
