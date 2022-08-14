package io.wafflestudio.spring.slack

import io.wafflestudio.spring.slack.webflux.SlackWebExceptionHandler
import io.wafflestudio.spring.slack.webmvc.SlackExceptionResolver
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlackAutoConfiguration {

    @Bean
    fun slackClient(): SlackClient {
        TODO()
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
    class SentryWebfluxConfiguration {

        @Bean
        fun exceptionHandler(): SlackWebExceptionHandler {
            TODO()
        }
    }
}
