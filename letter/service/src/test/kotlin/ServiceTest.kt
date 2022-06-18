package waffle.guam.favorite.service

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Primary
import org.springframework.context.event.EventListener
import org.springframework.http.codec.multipart.FilePart
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.stereotype.Service
import org.springframework.test.context.event.BeforeTestExecutionEvent
import waffle.guam.favorite.service.command.ImageCommandService
import waffle.guam.favorite.service.domain.User
import waffle.guam.favorite.service.query.UserQueryService

@SpringBootTest(properties = ["spring.cloud.vault.enabled=false"])
annotation class ServiceTest

@SpringBootApplication
class ServiceTestApplication(
    private val applicationContext: ApplicationContext,
    private val connectionFactory: ConnectionFactory,
) {
    @EventListener(BeforeTestExecutionEvent::class)
    fun emptyDatabase(): Unit = runBlocking {
        ResourceDatabasePopulator(applicationContext.getResource("classpath:/schema.sql"))
            .populate(connectionFactory)
            .awaitSingleOrNull()
    }

    @Primary
    @Service
    class MockUserQueryService : UserQueryService {
        override suspend fun get(userId: Long): User =
            mockUser.copy(userId)

        override suspend fun get(userIds: List<Long>): Map<Long, User> =
            userIds.associateWith { mockUser.copy(id = it) }

        companion object {
            private val mockUser = User(
                id = 0,
                introduction = null,
                githubId = null,
                blogUrl = null,
                nickname = null,
                email = null,
                profileImage = null,
                interests = listOf()
            )
        }
    }

    @Primary
    @Service
    class MockImageCommandService : ImageCommandService {
        override suspend fun upload(letterId: Long, image: FilePart): String = "DEV/LETTER/$letterId/1.png"
    }
}
