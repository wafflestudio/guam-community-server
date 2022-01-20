package waffle.guam.immigration.server.user

import org.springframework.stereotype.Service
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.api.user.UserServiceGrpcKt
import waffle.guam.immigration.api.user.UserServiceProto
import waffle.guam.immigration.api.user.toProto

@Service
class UserGrpcServer(
    private val impl: UserService
) : UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    override suspend fun getUser(request: UserServiceProto.GetUserRequest): UserServiceProto.GetUserResponse =
        impl.getUser(GetUserRequest(request)).toProto()
}
