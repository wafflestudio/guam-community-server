package waffle.guam.immigration.api.user

data class GetUserRequest(val token: String)

fun GetUserRequest.toProto(): UserServiceProto.GetUserRequest {
    val builder = UserServiceProto.GetUserRequest.newBuilder()

    builder.token = token

    return builder.build()
}

fun GetUserRequest(proto: UserServiceProto.GetUserRequest) =
    GetUserRequest(token = proto.token)
