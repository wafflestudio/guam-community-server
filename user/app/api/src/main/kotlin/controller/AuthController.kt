package waffle.guam.user.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.user.api.controller.AuthInfo.Info
import waffle.guam.user.infra.firebase.FirebaseClient
import waffle.guam.user.infra.firebase.FirebaseClient.ThirdPartyId.KakaoId
import waffle.guam.user.infra.kakao.KakaoClient
import waffle.guam.user.service.auth.AuthCommandService
import waffle.guam.user.service.auth.AuthCommandService.CreateUser
import waffle.guam.user.service.user.UserQueryService

@RestController
class AuthController(
    private val userService: UserQueryService,
    private val authService: AuthCommandService,
    private val firebaseService: FirebaseClient,
    private val kakaoService: KakaoClient,
) {

    @GetMapping("/api/v1/auth")
    fun getAuth(
        @RequestParam token: String,
    ): AuthInfo {
        val firebaseId = firebaseService.getUserInfoByToken(token)?.userId ?: return AuthInfo()

        val user = userService.getUser(firebaseId) ?: authService.createUser(CreateUser(firebaseId))

        return AuthInfo(Info(userId = user.id))
    }

    @GetMapping("/api/v1/user/token")
    fun initFirebaseToken(
        @RequestParam kakaoToken: String,
    ): TokenInfo {
        val kakaoId = kakaoService.getUserId(kakaoToken)?.let { KakaoId("$it") }
        requireNotNull(kakaoId)

        val firebaseUserInfo = firebaseService.run {
            val uid = resolveUserId(kakaoId)
            firebaseService.getUserInfoByUserId(uid) ?: firebaseService.createUserInfo(uid)
        }
        val firebaseCustomToken = firebaseService.getCustomToken(firebaseUserInfo.userId)

        return TokenInfo(firebaseCustomToken)
    }
}

data class AuthInfo(val user: Info? = null) {
    data class Info(
        val userId: Long,
        val deviceId: String? = null,
    )
}

data class TokenInfo(val customToken: String)
