package waffle.guam.gateway.filter

import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import waffle.guam.gateway.ImmigrationService
import waffle.guam.gateway.config.MissingHeaderException
import waffle.guam.gateway.config.UserNotFoundException
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Component("Guam")
class GuamGatewayFilterFactory(
    private val immigrationService: ImmigrationService,
) : AbstractGatewayFilterFactory<GuamGatewayFilterFactory.Config>(Config::class.java) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun apply(config: Config?) = GatewayFilter { exchange, chain ->
        mono { chain.filter(addUserHeader(exchange)) }
            .flatMap { it }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun addUserHeader(exchange: ServerWebExchange): ServerWebExchange {
        val requestBuilder = exchange.request.mutate()

        // Bearer token
        val token = exchange.request.headers.getFirst("Authorization")
            ?.split(" ")
            ?.getOrNull(1)
            ?: throw MissingHeaderException("필수 헤더가 존재하지 않습니다.")

        val user = measureTimedValue {
            immigrationService.getUser(token) ?: throw UserNotFoundException("유저 정보를 찾을 수 없습니다.")
        }.let { (value, duration) ->
            if (duration.inWholeMilliseconds > AUTHENTICATION_THRESHOLD) {
                logger.warn("Authentication Latency Alert [$duration]")
            }
            value
        }

        requestBuilder.header("X-GATEWAY-USER-ID", "${user.userId}")
        requestBuilder.header("X-GATEWAY-DEVICE-ID", user.deviceId)

        return exchange.mutate().request(requestBuilder.build()).build()
    }

    class Config

    companion object {
        private const val AUTHENTICATION_THRESHOLD = 100
    }
}
