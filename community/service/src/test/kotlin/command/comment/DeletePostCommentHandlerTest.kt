package waffle.guam.community.command.comment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.Forbidden
import waffle.guam.community.service.command.comment.DeletePostComment
import waffle.guam.community.service.command.comment.DeletePostCommentHandler

@Sql("classpath:command/comment/test.sql")
@DataJpaTest
internal class DeletePostCommentHandlerTest @Autowired constructor(
    postRepository: PostRepository,
    private val postCommentRepository: PostCommentRepository,
) {
    private val sut = DeletePostCommentHandler(postRepository)
    private val command = DeletePostComment(
        postId = 1L,
        userId = 1L,
        commentId = 1L,
    )

    @DisplayName("권한이 없는 유저는 삭제를 할 수 없다")
    @Test
    fun forbidden() {
        // given test.sql

        // when & then
        assertThrows<Forbidden> {
            sut.handle(command.copy(userId = 403))
        }
    }

    @DisplayName("게시글에 작성한 댓글을 삭제할 수 있다")
    @Test
    fun delete() {
        // given test.sql

        // when
        val result = sut.handle(command)

        // then
        val comment = postCommentRepository.getById(result.commentId)
        assertThat(comment.status).isEqualTo(PostCommentEntity.Status.DELETED)
    }
}
