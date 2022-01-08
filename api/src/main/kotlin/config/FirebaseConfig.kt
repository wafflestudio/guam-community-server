package waffle.guam.community.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource

@Bean
fun firebaseAuth(): FirebaseAuth {
    initializeFirebase()
    return FirebaseAuth.getInstance()
}

fun initializeFirebase(): FirebaseApp =
    FirebaseApp.initializeApp(
        FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(ClassPathResource("waffle-guam-firebase-adminsdk-1o1hg-27c33a640a.json").inputStream)
            )
            .build()
    )
