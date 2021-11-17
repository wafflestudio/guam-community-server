package waffle.guam.community.controller.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import waffle.guam.community.config.firebaseAuth
import waffle.guam.community.controller.auth.req.KaKaoRequestMeResponse

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val firebaseAuthInstance: FirebaseAuth = firebaseAuth()
) {
    private val webClient: WebClient = WebClient.create("https://kapi.kakao.com/v2/user/me")

    @GetMapping("")
    fun getFirebaseToken(
        @RequestParam token: String
    ): Mono<Response> =
        webClient.get()
            .headers { it.set("Authorization", "Bearer $token") }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(KaKaoRequestMeResponse::class.java)
            .map { Response(customToken = firebaseAuthInstance.createCustomToken(getOrCreateUID(it))) }

    // TODO logging
    fun getOrCreateUID(res: KaKaoRequestMeResponse): String =
        kotlin
            .runCatching { firebaseAuthInstance.getUser("guam:${res.id}").uid }
            .getOrElse {
                firebaseAuthInstance.createUser(
                    UserRecord.CreateRequest().also { it.setUid("guam:${res.id}") }
                ).uid
            }

    data class Response(
        val customToken: String,
    )
}
