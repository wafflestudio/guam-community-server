package waffle.guam.immigration.api.user

data class UpdateUserDeviceRequest(
    val token: String,
    val deviceId: String,
)

fun UpdateUserDeviceRequest.toProto(): UserServiceProto.UpdateUserDeviceRequest =
    UserServiceProto
        .UpdateUserDeviceRequest
        .newBuilder()
        .setToken(token)
        .setDeviceId(deviceId)
        .build()

fun UpdateUserDeviceRequest(proto: UserServiceProto.UpdateUserDeviceRequest) =
    UpdateUserDeviceRequest(
        token = proto.token,
        deviceId = proto.deviceId,
    )
