package waffle.guam.community.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import waffle.guam.community.common.InvalidFirebaseTokenException
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.auth.AuthService
import waffle.guam.community.controller.auth.FirebaseInfo
import javax.servlet.http.HttpServletRequest

interface GuamUserContextResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        UserContext::class.java.isAssignableFrom(parameter.parameterType)
}

@Component
@Profile("dev & prod")
class UserContextResolver(
    private val authService: AuthService,
) : GuamUserContextResolver {

    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UserContext {
        val req = (webRequest.nativeRequest as HttpServletRequest)
        val userContext = req.getHeader(HttpHeaders.AUTHORIZATION)
            ?.split(' ')
            ?.takeIf { parsedHeader ->
                "Bearer" == parsedHeader.first()
            }?.let { parsedHeader ->
                val token = parsedHeader.last()
                authService.verify(token)
            }
            ?: throw InvalidFirebaseTokenException("토큰 정보를 찾을 수 없습니다.")

        logger.info("[USER-${userContext.id}] ${req.method} : ${req.requestURI}")
        return userContext
    }
}

/**
 * Test 에서 UserContext 제공하는 법
 * Header 항목에 아래 추가
 * Authorization: {expectingId}
 */
@Component
@Profile("test")
class UserContextResolverForTest(
    private val authService: AuthService
) : GuamUserContextResolver {
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UserContext {
        val req = (webRequest.nativeRequest as HttpServletRequest)
        val user = req.getHeader(HttpHeaders.AUTHORIZATION)
            ?.let { firebaseUid ->
                authService.getOrCreateUser(FirebaseInfo(uid = firebaseUid, email = null, username = "test user"))
            } ?: throw InvalidFirebaseTokenException("토큰 정보를 찾을 수 없습니다.")
        return UserContext(user.id)
    }
}
