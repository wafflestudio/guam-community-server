package waffle.guam.community.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import waffle.guam.community.common.UserContext
import waffle.guam.community.data.jdbc.user.UserRepository

@Configuration
class WebConfig(
    private val sessionService: SessionService,
    private val userRepository: UserRepository
) : WebMvcConfigurer {

    @Value("\${spring.profiles.active:dev}")
    private val activeProfile: String = ""

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        print(activeProfile)
        when (activeProfile) {
            "test" -> resolvers.add(UserContextResolverForTest(userRepository))
            else -> resolvers.add(UserContextResolver(sessionService))
        }
    }
}

interface SessionService {
    fun getUserContext(token: String): UserContext
}

@Service
class SessionServiceImpl(
    private val userRepository: UserRepository,
) : SessionService {

    override fun getUserContext(token: String): UserContext =
        userRepository.findByFirebaseUid(getFirebaseUid(token))
            .orElseThrow { Exception("USER WITH FIREBASE TOKEN $token NOT FOUND") }
            .let { UserContext(it.id) }

    private fun getFirebaseUid(token: String): String =
        firebaseAuth().verifyIdToken(token).uid
}
