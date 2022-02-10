package waffle.guam.immigration.api.user

interface UserService {
    suspend fun getUser(request: GetUserRequest): GetUserResponse
    suspend fun sendPush(request: SendPushRequest)
}
