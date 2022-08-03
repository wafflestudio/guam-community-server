package waffle.guam.favorite.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Primary
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.test.context.event.AfterTestExecutionEvent
import org.springframework.test.context.event.BeforeTestExecutionEvent
import redis.embedded.RedisServer
import waffle.guam.favorite.data.redis.RedisConfig.Companion.COMMENT_LIKE_KEY
import waffle.guam.favorite.data.redis.RedisConfig.Companion.LIKE_KEY
import waffle.guam.favorite.data.redis.RedisConfig.Companion.SCRAP_KEY
import waffle.guam.favorite.service.command.Event
import waffle.guam.favorite.service.infra.Comment
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.FavoriteKafkaProducer
import waffle.guam.favorite.service.infra.Post

@SpringBootTest("spring.cloud.vault.enabled=false")
annotation class ServiceTest {
    @SpringBootApplication
    class ServiceTestApplication

    @Primary
    @Service
    class TestKafkaProducer : FavoriteKafkaProducer {
        override suspend fun send(event: Event) {}
    }

    @Primary
    @Service
    class TestCommunity : CommunityService {
        override suspend fun getPost(postId: Long): Post? =
            Post(id = postId, userId = 0, title = "", content = "", status = "", isAnonymous = false)

        override suspend fun getComment(commentId: Long): Comment? =
            Comment(id = commentId, postId = 0, userId = 0, content = "", status = "", isAnonymous = false)
    }

    @Service
    class TestRedisControl(
        private val redisTemplate: ReactiveStringRedisTemplate,
    ) {
        val redisServer = RedisServer(
            ClassPathResource("redis-server-7.0.0.mac").file,
            6379
        )

        val logger: Logger = LoggerFactory.getLogger(javaClass)

        @EventListener(BeforeTestExecutionEvent::class)
        fun start(): Unit = runBlocking {
            logger.debug("starting RedisServer...")

            runCatching {
                redisServer.start()
            }

            redisTemplate.delete(LIKE_KEY).awaitSingle()
            redisTemplate.delete(COMMENT_LIKE_KEY).awaitSingle()
            redisTemplate.delete(SCRAP_KEY).awaitSingle()
        }

        @EventListener(AfterTestExecutionEvent::class)
        fun stop() {
            logger.debug("stopping RedisServer...")

            runCatching {
                redisServer.stop()
            }
        }
    }
}
