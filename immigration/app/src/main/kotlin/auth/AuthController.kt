package waffle.guam.immigration.app.auth

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UserService
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

    data class TokenResponse(val customToken: String)
}
