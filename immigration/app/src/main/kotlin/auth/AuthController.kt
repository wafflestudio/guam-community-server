package waffle.guam.immigration.app.auth

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UserService
import waffle.guam.immigration.app.auth.req.UpdateUserDevice
import waffle.guam.immigration.app.config.UserContext
import waffle.guam.immigration.server.user.FirebaseTokenHandler
import waffle.guam.immigration.server.user.UpdateDeviceHandler

@RequestMapping("/api/v1/auth")
@RestController
class AuthController(
    private val userService: UserService,
    private val tokenHandler: FirebaseTokenHandler,
    private val updateDeviceHandler: UpdateDeviceHandler,
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
        userContext: UserContext,
        @RequestBody request: UpdateUserDevice,
    ) = updateDeviceHandler.handle(userContext.id, request.deviceId)

    data class TokenResponse(val customToken: String)
}
