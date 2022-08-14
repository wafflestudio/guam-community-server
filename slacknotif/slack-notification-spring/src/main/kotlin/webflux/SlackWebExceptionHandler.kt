package io.wafflestudio.spring.slack.webflux

import io.wafflestudio.spring.slack.SlackClient
import io.wafflestudio.spring.slack.SlackEvent
import org.springframework.core.annotation.Order
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

@Order(-2)
class SlackWebExceptionHandler(
    private val slackClient: SlackClient,
) : WebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        if (ex !is ResponseStatusException) {
            val event = SlackEvent(exchange, ex)

            slackClient.captureEvent(event)
        }

        return Mono.error(ex)
    }
}
