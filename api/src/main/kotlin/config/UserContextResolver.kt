package waffle.guam.community.config

import com.google.firebase.auth.FirebaseAuthException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.MethodParameter
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import waffle.guam.community.common.InvalidFirebaseTokenException
import waffle.guam.community.common.UserContext
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import javax.servlet.http.HttpServletRequest

interface GuamUserContextResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        UserContext::class.java.isAssignableFrom(parameter.parameterType)
}

@Component
@Profile("dev & prod")
class UserContextResolver(
    private val sessionService: SessionService,
) : GuamUserContextResolver {

    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UserContext {
        val req = (webRequest.nativeRequest as HttpServletRequest)
        val userContext = req.getHeader(HttpHeaders.AUTHORIZATION)?.let {
            kotlin.runCatching {
                sessionService.getUserContext(it)
            }.getOrElse {
                if (it is FirebaseAuthException && it.message?.contains("expired") == true) {
                    throw InvalidFirebaseTokenException("만료된 토큰입니다.")
                }
                throw InvalidFirebaseTokenException("잘못된 토큰입니다.")
            }
        } ?: throw InvalidFirebaseTokenException("토큰 정보를 찾을 수 없습니다.")

        logger.info("[USER-${userContext.id}] ${req.method} : ${req.requestURI}")
        return userContext
    }
}

@Component
@Profile("test")
class UserContextResolverForTest(
    private val userRepository: UserRepository
) : GuamUserContextResolver {

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UserContext {
        // TODO 로직 고도화
        val user = userRepository.findByIdOrNull(1) ?: userRepository.save(UserEntity(firebaseUid = "", username = "test"))
        return UserContext(user.id)
    }
}
