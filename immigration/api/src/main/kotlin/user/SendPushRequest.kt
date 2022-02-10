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
        imagePath = proto.imageUrl
    )

fun SendPushRequest.toProto(): UserServiceProto.SendUserPush {
    return UserServiceProto
        .SendUserPush
        .newBuilder()
        .addAllUserIds(userIds)
        .setTitle(title)
        .setBody(body)
        .apply { imagePath?.let { this@apply.imageUrl = it } }
        .build()
}
