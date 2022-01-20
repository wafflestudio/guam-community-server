package waffle.guam.immigration.api.user

data class GetUserResponse(val user: User?)

fun GetUserResponse.toProto(): UserServiceProto.GetUserResponse {
    val builder = UserServiceProto.GetUserResponse.newBuilder()

    user?.let { builder.user = user.toProto() }

    return builder.build()
}

fun GetUserResponse(proto: UserServiceProto.GetUserResponse) =
    GetUserResponse(
        user = run {
            if (proto.hasUser()) {
                User(proto.user)
            } else {
                null
            }
        }
    )
