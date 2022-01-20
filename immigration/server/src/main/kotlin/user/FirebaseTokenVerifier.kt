package waffle.guam.immigration.server.user

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import kotlin.coroutines.resumeWithException

@Service
class FirebaseTokenVerifier {
    private val firebaseAuth = run {
        initFirebase()
        FirebaseAuth.getInstance()
    }

    suspend fun getFirebaseUid(token: String): String =
        runCatching { firebaseAuth.verifyIdTokenAsync(token).await().uid }
            .getOrElse { TODO() }

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
}
