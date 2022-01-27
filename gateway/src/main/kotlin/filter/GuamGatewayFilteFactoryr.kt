package waffle.guam.gateway.filter

import kotlinx.coroutines.reactor.mono
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.client.user.UserGrpcClient

@Component("Guam")
class GuamGatewayFilterFactory(
    env: Environment,
) : AbstractGatewayFilterFactory<GuamGatewayFilterFactory.Config>(Config::class.java) {
    val immigrationClient = when {
        "dev" in env.activeProfiles -> UserGrpcClient("dev")
        else -> TODO("Not yet implemented")
    }

    override fun apply(config: Config?) = GatewayFilter { exchange, chain ->
        mono { chain.filter(addUserHeader(exchange)) }
            .flatMap { it }
    }

    private suspend fun addUserHeader(exchange: ServerWebExchange): ServerWebExchange {
        val requestBuilder = exchange.request.mutate()

        // Bearer token
        val token = exchange.request.headers.getFirst("Authorization")
            ?.split(" ")
            ?.get(0)
            .let(::requireNotNull)

        val user = immigrationClient
            .getUser(GetUserRequest(token))
            .user
            ?: error("TODO")

        requestBuilder.header("X-GATEWAY-USER-ID", "${user.id}")
        requestBuilder.header("X-GATEWAY-DEVICE-ID", user.deviceId ?: "")

        return exchange.mutate().request(requestBuilder.build()).build()
    }

    class Config
}
