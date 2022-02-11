package waffle.guam.immigration.api.user

data class SendPushRequest(
    val userIds: List<Long>,
    val title: String,
    val body: String,
    val imagePath: String?,
)

fun sendPushRequest(proto: UserServiceProto.SendUserPush): SendPushRequest =
    SendPushRequest(
        userIds = proto.userIdsList,
        title = proto.title,
        body = proto.body,
        imagePath = if (proto.hasImageUrl()) {
            proto.imageUrl.value
        } else null,
    )

fun SendPushRequest.toProto(): UserServiceProto.SendUserPush {
    return UserServiceProto
        .SendUserPush
        .newBuilder()
        .addAllUserIds(userIds)
        .setTitle(title)
        .setBody(body)
        .also { builder ->
            imagePath?.let { builder.imageUrlBuilder.value = it }
        }.build()
}
