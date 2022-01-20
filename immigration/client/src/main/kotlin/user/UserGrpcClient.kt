package waffle.guam.immigration.client.user

import io.grpc.Channel
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.api.user.UserServiceGrpcKt
import waffle.guam.immigration.api.user.toProto

class UserGrpcClient(channel: Channel) : UserService {
    private val stub = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

    override suspend fun getUser(request: GetUserRequest): GetUserResponse =
        GetUserResponse(stub.getUser(request.toProto()))
}
