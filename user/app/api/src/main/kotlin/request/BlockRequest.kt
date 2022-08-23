package waffle.guam.user.api.request

data class CreateBlockRequest(
    val blockUserId: Long,
)

data class DeleteBlockRequest(
    val blockUserId: Long,
)
