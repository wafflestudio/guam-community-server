package waffle.guam.immigration.client.user

import io.grpc.Channel
import io.grpc.ManagedChannelBuilder
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.SendPushRequest
import waffle.guam.immigration.api.user.UpdateUserDeviceRequest
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.api.user.UserServiceGrpcKt
import waffle.guam.immigration.api.user.toProto

class UserGrpcClient constructor(channel: Channel) : UserService {
    constructor(env: String) : this(
        when (env) {
            "dev" -> {
                ManagedChannelBuilder
                    .forAddress("guam-immigration.jon-snow-korea.com", 80)
                    .usePlaintext()
                    .build()
            }
            else -> {
                TODO("Not Implemented yet.")
            }
        }
    )

    private val stub = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

    override suspend fun getUser(request: GetUserRequest): GetUserResponse =
        GetUserResponse(stub.getUser(request.toProto()))

    override suspend fun sendPush(request: SendPushRequest) {
        stub.sendPush(request.toProto())
    }

    override suspend fun updateUserDevice(request: UpdateUserDeviceRequest) {
        stub.updateUserDevice(request.toProto())
    }
}
