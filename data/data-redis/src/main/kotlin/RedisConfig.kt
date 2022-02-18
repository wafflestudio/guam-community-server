package waffle.guam.community.data.redis

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.stereotype.Component
import redis.embedded.RedisServer
import java.time.Duration
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
@Import(RedisAutoConfiguration::class)
class RedisConfig {

    // registered only when properties specified
    @ConditionalOnProperty("spring.redis.host")
    @Bean
    fun lettuceConnectionFactory(
        redisProperties: RedisProperties,
    ): LettuceConnectionFactory {

        // TODO: pool, client resources config
        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(500))
            .shutdownTimeout(Duration.ZERO)
            .build()

        val config = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port).apply {
            setPassword(redisProperties.password)
        }

        return LettuceConnectionFactory(
            config,
            clientConfig
        )
    }

    // registered only when properties missing
    @ConditionalOnProperty("spring.redis.host", matchIfMissing = true, havingValue = "value_never_matched")
    @Component
    class LocalRedis(
        lettuceConnectionFactory: LettuceConnectionFactory
    ) {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
        private val redisServer: RedisServer = RedisServer(lettuceConnectionFactory.port)

        @PostConstruct
        fun startRedis() =
            runCatching { redisServer.start() }
                .exceptionOrNull()
                ?.let { logger.error("LOCAL REDIS FAILED TO START.", it) }
                ?: logger.warn("LOCAL REDIS HAS STARTED")

        @PreDestroy
        fun stopRedis() =
            runCatching { redisServer.stop() }
                .exceptionOrNull()
                ?.let { logger.error("LOCAL REDIS FAILED TO STOP.", it) }
                ?: logger.warn("LOCAL REDIS HAS STOPPED.")
    }
}
