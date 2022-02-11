package waffle.guam.immigration.api

import org.junit.jupiter.api.Test
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UpdateUserDeviceRequest
import waffle.guam.immigration.api.user.User
import waffle.guam.immigration.api.user.toProto

class UserProtoTest {
    @Test
    fun user() {
        val user = User(id = 1, deviceId = "1234")

        assert(user == User(user.toProto()))

        val user2 = User(id = 2, deviceId = null)

        assert(user2 == User(user2.toProto()))
    }

    @Test
    fun getUserRequest() {
        val request = GetUserRequest(token = "1234")

        assert(request == GetUserRequest(request.toProto()))
    }

    @Test
    fun getUserResponse() {
        val response = GetUserResponse(User(id = 1, deviceId = "1234"))

        assert(response == GetUserResponse(response.toProto()))

        val response2 = GetUserResponse(User(id = 2, deviceId = null))

        assert(response2 == GetUserResponse(response2.toProto()))

        val response3 = GetUserResponse(null)

        assert(response3 == GetUserResponse(response3.toProto()))
    }

    @Test
    fun updateUserDeviceRequest() {
        val request = UpdateUserDeviceRequest(token = "a", deviceId = "b")

        assert(request == UpdateUserDeviceRequest(request.toProto()))
    }
}
