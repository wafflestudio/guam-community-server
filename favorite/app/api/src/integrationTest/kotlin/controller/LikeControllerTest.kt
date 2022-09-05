package waffle.guam.favorite.api.controller

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import waffle.guam.favorite.api.IntegrationTest
import waffle.guam.favorite.api.withUser
import waffle.guam.favorite.data.r2dbc.entity.LikeEntity
import waffle.guam.favorite.data.r2dbc.repository.LikeRepository

@IntegrationTest
class LikeControllerTest @Autowired constructor(
    private val webClient: WebTestClient,
    private val likeRepository: LikeRepository,
) {

    @Test
    fun like(): Unit = runBlocking {
        webClient.withUser(userId = userId) {
            removeLike(userId = userId, postId = postId)

            // ok
            post()
                .uri("/api/v1/likes/posts/$postId")
                .exchange()
                .expectStatus().isOk

            // duplicate
            post()
                .uri("/api/v1/likes/posts/$postId")
                .exchange()
                .expectStatus().isEqualTo(409)
        }
    }

    @Test
    fun unLike(): Unit = runBlocking {
        webClient.withUser(userId = userId) {
            // not found
            delete()
                .uri("/api/v1/likes/posts/$postId")
                .exchange()
                .expectStatus().isNotFound

            createLike(userId = userId, postId = postId)

            // ok
            delete()
                .uri("/api/v1/likes/posts/$postId")
                .exchange()
                .expectStatus().isOk
        }
    }

    // setup
    private val userId = 34L
    private val postId = 5L

    @AfterEach
    fun afterEach(): Unit = runBlocking { removeLike(userId = userId, postId = postId) }

    private suspend fun createLike(userId: Long, postId: Long) {
        likeRepository.save(LikeEntity(postId = postId, userId = userId))
    }

    private suspend fun removeLike(userId: Long, postId: Long) {
        likeRepository.deleteByPostIdAndUserId(postId = postId, userId = userId)
    }
}
