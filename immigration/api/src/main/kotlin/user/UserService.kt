package waffle.guam.immigration.api.user

interface UserService {
    suspend fun getUser(request: GetUserRequest): GetUserResponse
    suspend fun updateUserDevice(request: UpdateUserDeviceRequest)
    suspend fun sendPush(request: SendPushRequest)
}
