package waffle.guam.community.controller.auth

import com.google.firebase.auth.AuthErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import waffle.guam.community.common.InvalidFirebaseTokenException
import waffle.guam.community.common.UserContext
import waffle.guam.community.config.firebaseAuth
import waffle.guam.community.controller.auth.req.KaKaoRequestMeResponse
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.GuamConflict
import waffle.guam.community.service.command.user.UserCreated
import java.time.Duration

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val firebaseAuthInstance: FirebaseAuth = firebaseAuth()
) {
    private val webClient: WebClient = WebClient.create("https://kapi.kakao.com/v2/user/me")
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    fun kakaoLogin(token: String): String =
        webClient.get()
            .headers { it.set("Authorization", "Bearer $token") }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(KaKaoRequestMeResponse::class.java)
            .map { firebaseAuthInstance.createCustomToken(getOrCreateUID(it)) }
            .block(Duration.ofSeconds(5))
            ?: throw GuamConflict("다시 시도해주세요.")

    @Transactional
    fun verify(token: String): UserContext {
        val firebaseUid = getFirebaseUid(token)
        val userId = getOrCreateUser(firebaseUid = firebaseUid).id
        return UserContext(id = userId)
    }

    @Transactional
    fun getOrCreateUser(firebaseUid: String): UserEntity =
        userRepository.findByFirebaseUid(firebaseUid = firebaseUid)
            ?: userRepository.save(UserEntity(firebaseUid = firebaseUid))
                .also { logger.info("${UserCreated(it.id, it.firebaseUid)}") }

    private fun getFirebaseUid(token: String): String =
        runCatching {
            firebaseAuth().verifyIdToken(token).uid
        }.getOrElse { exc ->
            when ((exc as? FirebaseAuthException)?.authErrorCode) {
                AuthErrorCode.EXPIRED_ID_TOKEN ->
                    throw InvalidFirebaseTokenException("만료된 토큰입니다.")
                else ->
                    throw InvalidFirebaseTokenException("잘못된 토큰입니다.")
            }
        }

    private fun getOrCreateUID(res: KaKaoRequestMeResponse): String =
        runCatching {
            firebaseAuthInstance.getUser("guam:${res.id}").uid
        }.onFailure { exc ->
            logger.error("GET FIREBASE USER FAILED: UID [guam:${res.id}]", exc)
        }.getOrElse {
            val createRequest = UserRecord.CreateRequest().setUid("guam:${res.id}")
            firebaseAuthInstance.createUser(createRequest).uid
        }
}
