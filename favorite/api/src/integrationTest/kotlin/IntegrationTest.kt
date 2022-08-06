package waffle.guam.favorite.api

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
@SpringBootTest(classes = [GuamLikeApplication::class])
@ActiveProfiles("dev")
annotation class IntegrationTest

inline fun <T> WebTestClient.withUser(userId: Long, block: WebTestClient.() -> T) {
    this.mutate()
        .defaultHeaders { it.set("X-GATEWAY-USER-ID", "$userId") }
        .build()
        .run(block)
}
