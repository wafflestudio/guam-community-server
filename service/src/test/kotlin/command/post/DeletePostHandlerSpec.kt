package waffle.guam.community.command.post

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.Forbidden
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.post.DeletePost
import waffle.guam.community.service.command.post.DeletePostHandler

@Sql("classpath:/command/post/test.sql")
@DataJpaTest
@Transactional
class DeletePostHandlerSpec @Autowired constructor(
    private val postRepository: PostRepository,
) {
    private val handler = DeletePostHandler(postRepository)
    private val command = DeletePost(postId = 1, userId = 1)

    @DisplayName("해당 포스트가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun deleteNotExistingPost() {
        assertThrows<PostNotFound> {
            handler.handle(command.copy(postId = 404L))
        }
    }

    @DisplayName("요청자가 해당 포스트의 작성자가 아니면 에러가 발생한다.")
    @Test
    fun deleteNotMyPost() {
        assertThrows<Forbidden> {
            handler.handle(command.copy(userId = 401L))
        }
    }

    @DisplayName("요청이 유효하면 성공적으로 삭제한다.")
    @Test
    fun deletePostSuccessfully() {
        val result = handler.handle(command)
        val deletedPost = postRepository.findByIdOrNull(command.postId)

        assertThat(deletedPost).isNotEqualTo(null)
        assertThat(deletedPost!!.status).isEqualTo(PostEntity.Status.DELETED)
        assertThat(deletedPost.id).isEqualTo(result.postId)
        assertThat(deletedPost.boardId).isEqualTo(result.boardId)
        assertThat(deletedPost.user.id).isEqualTo(result.userId)
    }
}
