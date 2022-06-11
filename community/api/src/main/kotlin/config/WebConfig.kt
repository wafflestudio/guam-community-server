package waffle.guam.community.config

import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import waffle.guam.community.UserContext
import waffle.guam.community.service.GuamUnAuthorized
import waffle.guam.community.service.Log
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserContextResolver())
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(GuamRequestInterceptor())
    }
}

class GuamRequestInterceptor : HandlerInterceptor {
    companion object : Log

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.getHeader("X-REQUEST-ID")?.let { MDC.put("X-REQUEST-ID", it) }

        val userId = request.getHeader("X-GATEWAY-USER-ID")
        val uri = request.requestURI
        val params = request.parameterMap.map { it.key to it.value.toList() }

        log.info("userId: $userId, uri: $uri, params: $params")

        return super.preHandle(request, response, handler)
    }
}

class UserContextResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        UserContext::class.java.isAssignableFrom(parameter.parameterType)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UserContext {
        val req = (webRequest.nativeRequest as HttpServletRequest)

        return req.getHeader(GATEWAY_HEADER_NAME)?.toLong()?.let(::UserContext)
            ?: throw MissingHeaderException()
    }
}

const val GATEWAY_HEADER_NAME = "X-GATEWAY-USER-ID"

class MissingHeaderException(message: String = "헤더 정보를 찾을 수 없습니다.") : GuamUnAuthorized(message)
