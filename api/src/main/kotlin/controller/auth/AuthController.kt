package waffle.guam.community.controller.auth

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @GetMapping("/kakao")
    fun getFirebaseToken(
        @RequestParam token: String,
    ) = Response(customToken = authService.kakaoLogin(token))
}

data class Response(val customToken: String)
