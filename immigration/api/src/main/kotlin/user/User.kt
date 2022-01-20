package waffle.guam.immigration.api.user

data class User(
    val id: Long,
    val deviceId: String?,
)

fun User.toProto(): UserProto.User {
    val builder = UserProto.User.newBuilder()

    builder.id = id
    deviceId?.let { builder.deviceIdBuilder.value = it }

    return builder.build()
}

fun User(proto: UserProto.User) =
    User(
        id = proto.id,
        deviceId = run {
            if (proto.hasDeviceId()) {
                proto.deviceId.value
            } else {
                null
            }
        }
    )