package waffle.guam.user.infra.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.AuthErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord.CreateRequest
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import waffle.guam.user.infra.firebase.FirebaseClient.FirebaseTokenExpired
import waffle.guam.user.infra.firebase.FirebaseClient.ThirdPartyId

interface FirebaseClient {
    fun getCustomToken(uid: String): String
    fun getUserInfoByUserId(userId: String): FirebaseUserId?
    fun getUserInfoByToken(token: String): FirebaseUserId?
    fun createUserInfo(uid: String): FirebaseUserId
    fun resolveUserId(thirdPartyId: ThirdPartyId): String

    class FirebaseTokenExpired : RuntimeException()

    sealed class ThirdPartyId {
        abstract val id: String

        class KakaoId(override val id: String) : ThirdPartyId()
    }
}

@Service
class FirebaseClientImpl : FirebaseClient {
    private val firebaseAuth: FirebaseAuth by lazy {
        initFirebase()
        FirebaseAuth.getInstance()
    }

    override fun getCustomToken(uid: String): String =
        firebaseAuth.createCustomToken(uid)

    override fun getUserInfoByUserId(userId: String): FirebaseUserId? =
        runCatching {
            firebaseAuth.getUser(userId).uid
        }.recover {
            if (it is FirebaseAuthException && it.authErrorCode == AuthErrorCode.USER_NOT_FOUND) {
                null
            } else {
                throw it
            }
        }.getOrThrow()?.let(::FirebaseUserId)

    override fun getUserInfoByToken(token: String): FirebaseUserId? =
        runCatching {
            firebaseAuth.verifyIdToken(token).uid
        }.recover {
            if (it is FirebaseAuthException && it.authErrorCode == AuthErrorCode.USER_NOT_FOUND) {
                null
            } else if (it is FirebaseAuthException && it.authErrorCode == AuthErrorCode.EXPIRED_ID_TOKEN) {
                throw FirebaseTokenExpired()
            } else {
                throw it
            }
        }.getOrThrow()?.let(::FirebaseUserId)

    override fun createUserInfo(uid: String): FirebaseUserId =
        firebaseAuth.createUser(CreateRequest().setUid(uid))
            .let { FirebaseUserId(it.uid) }

    override fun resolveUserId(thirdPartyId: ThirdPartyId): String =
        "guam:${thirdPartyId.id}"

    private fun initFirebase() {
        FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(ClassPathResource("waffle-guam-firebase-adminsdk-1o1hg-27c33a640a.json").inputStream))
                .build()
        )
    }
}
