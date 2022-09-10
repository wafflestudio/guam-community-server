package waffle.guam.favorite.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import waffle.guam.favorite.data.redis.repository.PostScrapCountRepository
import waffle.guam.favorite.service.command.DuplicateScrapException
import waffle.guam.favorite.service.command.ScrapCreateHandler
import waffle.guam.favorite.service.command.ScrapDeleteHandler
import waffle.guam.favorite.service.command.ScrapNotFoundException
import waffle.guam.favorite.service.model.Scrap

@ServiceTest
class ScrapTest @Autowired constructor(
    private val createHandler: ScrapCreateHandler,
    private val deleteHandler: ScrapDeleteHandler,
    private val scrapCountRepository: PostScrapCountRepository,
    private val tx: TransactionalOperator,
) {
    @Test
    fun `한 게시물에 여러번 스크랩을 누를 수 없다`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            createHandler.handle(Scrap(postId = 1L, userId = 1L))

            assertThrows<DuplicateScrapException> {
                createHandler.handle(Scrap(postId = 1L, userId = 1L))
            }
        }
    }

    @Test
    fun `스크랩을 누르지 않은 게시물에 스크랩 취소를 할 수 없다`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            assertThrows<ScrapNotFoundException> {
                deleteHandler.handle(Scrap(postId = 1L, userId = 1L))
            }
        }
    }

    @Test
    fun `사가 테스트`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            createHandler.handle(Scrap(postId = 1L, userId = 1L))
            createHandler.handle(Scrap(postId = 2L, userId = 1L))
            createHandler.handle(Scrap(postId = 3L, userId = 1L))
            createHandler.handle(Scrap(postId = 3L, userId = 2L))

            Assertions.assertThat(scrapCountRepository.gets(listOf(1L, 2L, 3L)))
                .isEqualTo(mapOf(1L to 1L, 2L to 1L, 3L to 2L))

            deleteHandler.handle(Scrap(postId = 3L, userId = 2L))

            Assertions.assertThat(scrapCountRepository.gets(listOf(1L, 2L, 3L)))
                .isEqualTo(mapOf(1L to 1L, 2L to 1L, 3L to 1L))
        }
    }
}
