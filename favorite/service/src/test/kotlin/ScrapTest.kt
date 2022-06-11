package waffle.guam.favorite.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import waffle.guam.favorite.service.command.DuplicateScrapException
import waffle.guam.favorite.service.command.ScrapCreateHandler
import waffle.guam.favorite.service.command.ScrapDeleteHandler
import waffle.guam.favorite.service.command.ScrapNotFoundException
import waffle.guam.favorite.service.model.Scrap
import waffle.guam.favorite.service.query.ScrapCountStore

@ServiceTest
class ScrapTest @Autowired constructor(
    private val createHandler: ScrapCreateHandler,
    private val deleteHandler: ScrapDeleteHandler,
    private val scrapCountStore: ScrapCountStore.Rank,
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

            Assertions.assertThat(scrapCountStore.getCount(1L)).isEqualTo(1)
            Assertions.assertThat(scrapCountStore.getCount(2L)).isEqualTo(1)
            Assertions.assertThat(scrapCountStore.getCount(3L)).isEqualTo(2)
            Assertions.assertThat(scrapCountStore.getCount(listOf(1L, 2L, 3L)))
                .isEqualTo(mapOf(1L to 1, 2L to 1, 3L to 2))

            deleteHandler.handle(Scrap(postId = 3L, userId = 2L))

            Assertions.assertThat(scrapCountStore.getCount(1L)).isEqualTo(1)
            Assertions.assertThat(scrapCountStore.getCount(2L)).isEqualTo(1)
            Assertions.assertThat(scrapCountStore.getCount(3L)).isEqualTo(1)
            Assertions.assertThat(scrapCountStore.getCount(listOf(1L, 2L, 3L)))
                .isEqualTo(mapOf(1L to 1, 2L to 1, 3L to 1))
        }
    }
}
