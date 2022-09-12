package waffle.guam.letter.api.config

import kotlinx.coroutines.reactor.asFlux
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import waffle.guam.favorite.service.query.BlockQueryService

@Configuration
class LetterConfig(private val blockQueryService: BlockQueryService): WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(LetterArgumentResolver(blockQueryService))
    }
}

class LetterArgumentResolver(
    private val blockQueryService: BlockQueryService,
): HandlerMethodArgumentResolver {
    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        val header = exchange.request.headers["X-GATEWAY-USER-ID"] ?: throw IllegalArgumentException()
        val userId = header.single().toLong()
        return blockQueryService.getBlockedPairs(userId).asFlux().collectList().map(::BlockFilter)
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == BlockFilter::class.java
    }
}

data class BlockFilter(
    val blockedPairs: List<Long>,
)
