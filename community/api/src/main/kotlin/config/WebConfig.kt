package waffle.guam.community.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import waffle.guam.community.UserContext
import waffle.guam.community.service.GuamUnAuthorized
import waffle.guam.community.service.Log

@Configuration
class WebConfig : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(UserContextResolver())
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerModules(
            JavaTimeModule(),
            KotlinModule.Builder().build(),
        )
    }
}

@Component
class GuamRequestFilter : WebFilter {
    companion object : Log

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        request.headers["X-REQUEST-ID"]?.let { MDC.put("X-REQUEST-ID", it.single()) }

        val userId = request.headers["X-GATEWAY-USER-ID"]
        val uri = request.uri
        val params = request.queryParams.map { it.key to it.value.toList() }

        log.info("userId: $userId, uri: $uri, params: $params")

        return chain.filter(exchange)
    }
}

class UserContextResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        UserContext::class.java.isAssignableFrom(parameter.parameterType)

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        val header = exchange.request.headers[GATEWAY_HEADER_NAME]?.singleOrNull()
            ?: throw MissingHeaderException()

        return Mono.just(header.toLong().let(::UserContext))
    }
}

const val GATEWAY_HEADER_NAME = "X-GATEWAY-USER-ID"

class MissingHeaderException(message: String = "헤더 정보를 찾을 수 없습니다.") : GuamUnAuthorized(message)
