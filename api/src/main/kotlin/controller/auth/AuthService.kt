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
        val firebaseInfo = getFirebaseInfo(token)
        val user = getOrCreateUser(firebaseInfo = firebaseInfo)
        return UserContext(user)
    }

    @Transactional
    fun getOrCreateUser(firebaseInfo: FirebaseInfo): UserEntity {
        val email = firebaseInfo.email?.takeUnless { it == "null" }
        val username = firebaseInfo.username?.takeUnless { it == "null" }

        return userRepository.findByFirebaseUid(firebaseUid = firebaseInfo.uid)
            ?: createUser(UserEntity(firebaseUid = firebaseInfo.uid, email = email, nickname = username))
    }

    private fun getFirebaseInfo(token: String): FirebaseInfo =
        runCatching {
            val res = firebaseAuth().verifyIdToken(token)
            val username = (res.claims["name"] as? String)
            val email = (res.claims["email"] as? String)
            return FirebaseInfo(res.uid, email, username)
        }.getOrElse { exc ->
            when ((exc as? FirebaseAuthException)?.authErrorCode) {
                AuthErrorCode.EXPIRED_ID_TOKEN ->
                    throw InvalidFirebaseTokenException("만료된 토큰입니다.")
                else ->
                    throw InvalidFirebaseTokenException("잘못된 토큰입니다.")
            }
        }

    /**
     * 카카오 기본 제공 정보 이메일, 유저네임 가져오기
     * if null -> 파이어베이스에도 문자 그대로 null 저장
     */
    private fun getOrCreateUID(res: KaKaoRequestMeResponse): String =
        runCatching {
            firebaseAuthInstance.getUser("guam:${res.id}").uid
        }.onFailure { exc ->
            logger.error("GET FIREBASE USER FAILED: UID [guam:${res.id}]", exc)
        }.getOrElse {
            val createRequest = UserRecord.CreateRequest()
                .setUid("guam:${res.id}")
                .setDisplayName(res.properties?.nickname.toString())
                .setEmail(res.kakao_account.email.toString())
            firebaseAuthInstance.createUser(createRequest).uid
        }

    private fun createUser(user: UserEntity): UserEntity =
        userRepository.save(user)
            .also { logger.info("${UserCreated(it.id, it.firebaseUid)}") }
}

class FirebaseInfo(
    val uid: String,
    val email: String?,
    val username: String?
)
