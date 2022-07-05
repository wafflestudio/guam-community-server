package waffle.guam.community.command.comment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.Forbidden
import waffle.guam.community.service.command.comment.UpdatePostComment
import waffle.guam.community.service.command.comment.UpdatePostCommentHandler

@Sql("classpath:/command/comment/test.sql")
@DataJpaTest
internal class UpdatePostCommentHandlerTest @Autowired constructor(
    postRepository: PostRepository,
    private val postCommentRepository: PostCommentRepository,
) {
    private val sut = UpdatePostCommentHandler(postRepository)
    private val command = UpdatePostComment(
        postId = 1L,
        userId = 1L,
        commentId = 1L,
        content = "어쩔티비",
    )

    @DisplayName("작성자가 아니면 댓글을 수정할 수 없다")
    @Test
    fun forbidden() {
        // given test.sql

        // when & then
        assertThrows<Forbidden> {
            sut.handle(command.copy(userId = 403))
        }
    }

    @DisplayName("게시글을 수정할 수 있다")
    @Test
    fun update() {
        // given test.sql

        // when
        val result = sut.handle(command)

        // then
        val comment = postCommentRepository.getById(result.commentId)
        assertThat(comment.content).isEqualTo("어쩔티비")
    }
}
