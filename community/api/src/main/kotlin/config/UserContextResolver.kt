package waffle.guam.community.config

import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import waffle.guam.community.common.MissingHeaderException
import waffle.guam.community.common.UserContext
import javax.servlet.http.HttpServletRequest

const val GATEWAY_HEADER_NAME = "X-GATEWAY-USER-ID"

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

        return req.getHeader(GATEWAY_HEADER_NAME)?.toLong()?.let(::UserContext) ?: throw MissingHeaderException()
    }
}
