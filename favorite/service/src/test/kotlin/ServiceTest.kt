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
import waffle.guam.favorite.service.infra.Comment
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.NotificationService
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest
import waffle.guam.favorite.service.infra.Post

@SpringBootTest("spring.cloud.vault.enabled=false")
annotation class ServiceTest {
    @SpringBootApplication
    class ServiceTestApplication

    @Primary
    @Service
    class TestNotificationService : NotificationService {
        override suspend fun notify(request: CreateNotificationRequest) {}
    }

    @Primary
    @Service
    class TestCommunity : CommunityService {
        override suspend fun getPost(postId: Long): Post? =
            basePost.copy(id = postId)

        override suspend fun getPosts(postIds: List<Long>): Map<Long, Post> =
            postIds.associateWith { basePost.copy(id = it) }

        override suspend fun getComment(commentId: Long): Comment? =
            baseComment.copy(id = commentId)

        private val basePost: Post =
            Post(id = 0, boardId = 1, userId = 0, title = "", content = "", status = "", isAnonymous = false)

        private val baseComment: Comment =
            Comment(id = 0, postId = 1, userId = 0, content = "", status = "", isAnonymous = false)
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
