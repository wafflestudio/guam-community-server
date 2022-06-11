package waffle.guam.favorite.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.test.context.event.BeforeTestExecutionEvent
import waffle.guam.favorite.data.redis.RedisConfig.Companion.COMMENT_LIKE_KEY
import waffle.guam.favorite.data.redis.RedisConfig.Companion.LIKE_KEY
import waffle.guam.favorite.data.redis.RedisConfig.Companion.SCRAP_KEY

@SpringBootTest("spring.cloud.vault.enabled=false")
annotation class ServiceTest {
    @SpringBootApplication
    class ServiceTestApplication

    @Service
    class TestRedisControl(
        private val redisTemplate: ReactiveStringRedisTemplate,
    ) {

        @EventListener(BeforeTestExecutionEvent::class)
        fun start(): Unit = runBlocking {
            redisTemplate.delete(LIKE_KEY).awaitSingle()
            redisTemplate.delete(COMMENT_LIKE_KEY).awaitSingle()
            redisTemplate.delete(SCRAP_KEY).awaitSingle()
        }
    }
}
