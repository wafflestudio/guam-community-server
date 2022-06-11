package waffle.guam.favorite.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import waffle.guam.favorite.service.command.DuplicateLikeException
import waffle.guam.favorite.service.command.LikeCreateHandler
import waffle.guam.favorite.service.command.LikeDeleteHandler
import waffle.guam.favorite.service.command.LikeNotFoundException
import waffle.guam.favorite.service.model.Like
import waffle.guam.favorite.service.query.LikeCountStore

@ServiceTest
class LikeTest @Autowired constructor(
    private val likeCreateHandler: LikeCreateHandler,
    private val likeDeleteHandler: LikeDeleteHandler,
    private val likeRankStore: LikeCountStore.Rank,
    private val likeCountStore: LikeCountStore,
    private val tx: TransactionalOperator,
) {
    @Test
    fun `한 게시물에 여러번 좋아요를 누를 수 없다`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            likeCreateHandler.handle(Like(postId = 1L, userId = 1L))
            assertThrows<DuplicateLikeException> {
                likeCreateHandler.handle(Like(postId = 1L, userId = 1L))
            }
        }
    }

    @Test
    fun `좋아요 누르지 않은 게시물에 좋아요 취소를 할 수 없다`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            assertThrows<LikeNotFoundException> {
                likeDeleteHandler.handle(Like(postId = 1L, userId = 1L))
            }
        }
    }

    @Test
    fun `게시물의 좋아요 갯수에 따라 랭킹이 바뀐다`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            // 순위 존재 x
            assertThat(likeRankStore.getRank(0, 100)).isEqualTo(emptyList<Long>())
            assertThat(likeCountStore.getCount(1L)).isEqualTo(0)
            assertThat(likeCountStore.getCount(2L)).isEqualTo(0)
            assertThat(likeCountStore.getCount(3L)).isEqualTo(0)
            assertThat(likeCountStore.getCount(listOf(1L, 2L, 3L))).isEqualTo(mapOf(1L to 0, 2L to 0, 3L to 0))

            // 좋아요 추가
            likeCreateHandler.handle(Like(postId = 1L, userId = 1L))
            likeCreateHandler.handle(Like(postId = 1L, userId = 2L))
            likeCreateHandler.handle(Like(postId = 2L, userId = 1L))
            likeCreateHandler.handle(Like(postId = 2L, userId = 2L))
            likeCreateHandler.handle(Like(postId = 2L, userId = 3L))
            likeCreateHandler.handle(Like(postId = 3L, userId = 1L))

            // 2(3개) > 1(2개) > 3(1개)
            assertThat(likeRankStore.getRank(0, 100)).isEqualTo(listOf(2L, 1L, 3L))
            assertThat(likeCountStore.getCount(2L)).isEqualTo(3)
            assertThat(likeCountStore.getCount(1L)).isEqualTo(2)
            assertThat(likeCountStore.getCount(3L)).isEqualTo(1)
            assertThat(likeCountStore.getCount(listOf(2L, 1L, 3L))).isEqualTo(mapOf(2L to 3, 1L to 2, 3L to 1))

            // 좋아요 삭제
            likeDeleteHandler.handle(Like(postId = 2L, userId = 1L))
            likeDeleteHandler.handle(Like(postId = 2L, userId = 2L))
            likeDeleteHandler.handle(Like(postId = 2L, userId = 3L))

            // 1(2개) > 3(1개) > 2(0개)
            assertThat(likeRankStore.getRank(0, 100)).isEqualTo(listOf(1L, 3L, 2L))
            assertThat(likeCountStore.getCount(1L)).isEqualTo(2)
            assertThat(likeCountStore.getCount(3L)).isEqualTo(1)
            assertThat(likeCountStore.getCount(2L)).isEqualTo(0)
            assertThat(likeCountStore.getCount(listOf(1L, 3L, 2L))).isEqualTo(mapOf(1L to 2, 3L to 1, 2L to 0))
        }
    }
}
