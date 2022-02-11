package waffle.guam.immigration.server.user

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import kotlin.coroutines.resumeWithException

// TODO: refactor
@Service
class FirebaseTokenHandler {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    private val firebaseAuth by lazy {
        initFirebase()
        FirebaseAuth.getInstance()
    }
    private val kakaoClient: WebClient = WebClient.create("https://kapi.kakao.com/v2/user/me")

    suspend fun getFirebaseUid(token: String): String =
        runCatching { firebaseAuth.verifyIdTokenAsync(token).await().uid }
            .getOrElse {
                // TODO: 에러 처리
                "test"
            }

    suspend fun getCustomToken(kakaoToken: String): String {
        val kakaoResponse = kakaoClient.get()
            .headers { it.set("Authorization", "Bearer $kakaoToken") }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody<KaKaoRequestMeResponse>()

        return firebaseAuth.createCustomTokenAsync(getOrCreateUID(kakaoResponse)).await()
    }

    private fun getOrCreateUID(res: KaKaoRequestMeResponse): String =
        runCatching {
            firebaseAuth.getUser("guam:${res.id}").uid
        }.onFailure { exc ->
            logger.error("GET FIREBASE USER FAILED: UID [guam:${res.id}]", exc)
        }.getOrElse {
            val createRequest = UserRecord.CreateRequest()
                .setUid("guam:${res.id}")
            firebaseAuth.createUser(createRequest).uid
        }

    private fun initFirebase() {
        FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(ClassPathResource("waffle-guam-firebase-adminsdk-1o1hg-27c33a640a.json").inputStream))
                .build()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun <T> ApiFuture<T>.await(): T = suspendCancellableCoroutine { continuation ->
        val callback = object : ApiFutureCallback<T> {
            override fun onSuccess(result: T) = continuation.resume(result) {
                this@await.cancel(true)
            }

            override fun onFailure(throwable: Throwable) = continuation.resumeWithException(throwable)
        }

        ApiFutures.addCallback(this@await, callback, MoreExecutors.directExecutor())
    }

    data class KaKaoRequestMeResponse(
        val id: Int,
    )
}
