package waffle.guam.immigration.app.auth

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UpdateUserDeviceRequest
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.app.auth.req.UpdateUserDevice
import waffle.guam.immigration.server.user.FirebaseTokenHandler

@RequestMapping("/api/v1/auth")
@RestController
class AuthController(
    private val userService: UserService,
    private val tokenHandler: FirebaseTokenHandler,
) {

    @GetMapping("/user")
    suspend fun getUser(
        @RequestParam token: String,
    ): GetUserResponse =
        userService.getUser(GetUserRequest(token))

    @GetMapping("/token")
    suspend fun initFirebaseToken(
        @RequestParam kakaoToken: String,
    ): TokenResponse =
        tokenHandler.getCustomToken(kakaoToken).let(::TokenResponse)

    @PatchMapping("/deviceToken")
    suspend fun userUpdate(
        @RequestHeader("Authorization") bearerToken: String,
        @RequestBody request: UpdateUserDevice,
    ) {
        val token = bearerToken.split(" ").getOrNull(1).let(::requireNotNull)
        userService.updateUserDevice(UpdateUserDeviceRequest(token, request.deviceId))
    }

    data class TokenResponse(val customToken: String)
}
