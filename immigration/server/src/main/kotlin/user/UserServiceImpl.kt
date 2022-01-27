package waffle.guam.immigration.server.user

import org.springframework.stereotype.Service
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.server.user.domain.User
import waffle.guam.immigration.server.user.domain.UserRepository

@Service
class UserServiceImpl(
    private val tokenHandler: FirebaseTokenHandler,
    private val userRepository: UserRepository,
) : UserService {
    override suspend fun getUser(request: GetUserRequest): GetUserResponse =
        tokenHandler.getFirebaseUid(request.token)
            .let { userRepository.findByFirebaseId(it) ?: userRepository.save(User(firebaseId = it)) }
            .let { ApiUser(it) }
            .let { GetUserResponse(it) }
}
