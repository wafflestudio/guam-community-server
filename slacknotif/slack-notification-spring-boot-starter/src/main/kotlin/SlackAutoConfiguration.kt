package io.wafflestudio.spring.slack

import io.wafflestudio.spring.slack.webflux.SlackWebExceptionHandler
import io.wafflestudio.spring.slack.webmvc.SlackExceptionResolver
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlackAutoConfiguration {

    @Bean
    fun slackClient(): SlackClient {
        // TODO
        return object : SlackClient {
            private val logger = LoggerFactory.getLogger(javaClass)

            override fun isEnabled() {
                logger.debug("isEnabled")
            }

            override fun captureEvent(e: SlackEvent) {
                logger.debug("captureEvent e: {}", e)
            }
        }
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Configuration
    class SentryWebMvcConfiguration {

        @Bean
        fun exceptionResolver(): SlackExceptionResolver {
            TODO()
        }
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Configuration
    class SentryWebfluxConfiguration(
        private val slackClient: SlackClient
    ) {

        @Bean
        fun exceptionHandler(): SlackWebExceptionHandler {
            // TODO
            return SlackWebExceptionHandler(slackClient)
        }
    }
}
