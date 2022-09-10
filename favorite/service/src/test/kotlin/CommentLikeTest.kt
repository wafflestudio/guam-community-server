package waffle.guam.favorite.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import waffle.guam.favorite.data.redis.repository.CommentLikeCountRepository
import waffle.guam.favorite.service.command.CommentLikeCreateHandler
import waffle.guam.favorite.service.command.CommentLikeDeleteHandler
import waffle.guam.favorite.service.command.CommentLikeNotFoundException
import waffle.guam.favorite.service.command.DuplicateCommentLikeException
import waffle.guam.favorite.service.model.CommentLike

@ServiceTest
class CommentLikeTest @Autowired constructor(
    private val createHandler: CommentLikeCreateHandler,
    private val deleteHandler: CommentLikeDeleteHandler,
    private val likeCountRepository: CommentLikeCountRepository,
    private val tx: TransactionalOperator,
) {

    @Test
    fun `한 댓글에 여러번 좋아요를 누를 수 없다`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            createHandler.handle(CommentLike(postCommentId = 1L, userId = 1L))

            assertThrows<DuplicateCommentLikeException> {
                createHandler.handle(CommentLike(postCommentId = 1L, userId = 1L))
            }
        }
    }

    @Test
    fun `좋아요 누르지 않은 댓글에 좋아요 취소를 할 수 없다`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            assertThrows<CommentLikeNotFoundException> {
                deleteHandler.handle(CommentLike(postCommentId = 1L, userId = 1L))
            }
        }
    }

    @Test
    fun `사가 테스트`(): Unit = runBlocking {
        tx.executeAndAwait {
            it.setRollbackOnly()

            createHandler.handle(CommentLike(postCommentId = 1L, userId = 1L))
            createHandler.handle(CommentLike(postCommentId = 2L, userId = 1L))
            createHandler.handle(CommentLike(postCommentId = 3L, userId = 1L))
            createHandler.handle(CommentLike(postCommentId = 3L, userId = 2L))

            Assertions.assertThat(likeCountRepository.gets(listOf(1L, 2L, 3L)))
                .isEqualTo(mapOf(1L to 1L, 2L to 1L, 3L to 2L))

            deleteHandler.handle(CommentLike(postCommentId = 3L, userId = 2L))

            Assertions.assertThat(likeCountRepository.gets(listOf(1L, 2L, 3L)))
                .isEqualTo(mapOf(1L to 1L, 2L to 1L, 3L to 1L))
        }
    }
}
