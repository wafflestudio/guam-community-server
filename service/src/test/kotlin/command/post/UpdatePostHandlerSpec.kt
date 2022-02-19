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
import waffle.guam.community.common.InvalidArgumentException
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.TagRepository
import waffle.guam.community.service.command.post.UpdatePost
import waffle.guam.community.service.command.post.UpdatePostHandler

@Sql("classpath:/command/post/test.sql")
@DataJpaTest
@Transactional
class UpdatePostHandlerSpec @Autowired constructor(
    private val postRepository: PostRepository,
    tagRepository: TagRepository,
) {
    private val handler = UpdatePostHandler(postRepository, tagRepository)
    private val command = UpdatePost(
        postId = 1L,
        userId = 1L,
        title = "Update Test",
        content = "This is update test",
        tagId = 2L
    )

    @DisplayName("해당 포스트가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun updateNotExistingPost() {
        assertThrows<PostNotFound> {
            handler.handle(command.copy(postId = 404L))
        }
    }

    @DisplayName("요청자가 해당 포스트의 작성자가 아니면 에러가 발생한다.")
    @Test
    fun updateNotMyPost() {
        assertThrows<Forbidden> {
            handler.handle(command.copy(userId = 401L))
        }
    }

    @DisplayName("요청의 모든 값이 null이면 에러가 발생한다.")
    @Test
    fun updateNothing() {
        assertThrows<InvalidArgumentException> {
            handler.handle(command.copy(title = null, content = null, tagId = null))
        }
    }

    @DisplayName("요청이 유효하면 성공적으로 업데이트한다.")
    @Test
    fun updateSuccessfully() {
        val result = handler.handle(command)
        val updatedPost = postRepository.findByIdOrNull(command.postId)

        assertThat(updatedPost).isNotEqualTo(null)
        updatedPost!!.run {
            assertThat(user.id).isEqualTo(command.userId)
            assertThat(title).isEqualTo(command.title)
            assertThat(content).isEqualTo(command.content)
            assertThat(tags.first().tag.id).isEqualTo(command.tagId)
        }

        result.run {
            assertThat(postId).isEqualTo(updatedPost.id)
            assertThat(boardId).isEqualTo(updatedPost.boardId)
            assertThat(userId).isEqualTo(updatedPost.user.id)
        }
    }

    @DisplayName("파라미터가 null이 아닌 값들만 성공적으로 업데이트한다.")
    @Test
    fun updateOnlyNotNullParams() {
        val partialNullCommand = command.copy(
            title = "This is Update Test2",
            content = null,
            tagId = null
        )
        val oldPost = postRepository.findByIdOrNull(partialNullCommand.postId)!!
        val result = handler.handle(partialNullCommand)
        val updatedPost = postRepository.findByIdOrNull(command.postId)

        assertThat(updatedPost).isNotEqualTo(null)

        updatedPost!!.run {
            assertThat(user.id).isEqualTo(partialNullCommand.userId)
            assertThat(title).isEqualTo(partialNullCommand.title)
            assertThat(content).isEqualTo(oldPost.content)
            assertThat(tags.first().tag.id).isEqualTo(oldPost.tags.first().tag.id)
        }

        result.run {
            assertThat(postId).isEqualTo(updatedPost.id)
            assertThat(boardId).isEqualTo(updatedPost.boardId)
            assertThat(userId).isEqualTo(updatedPost.user.id)
        }
    }
}
