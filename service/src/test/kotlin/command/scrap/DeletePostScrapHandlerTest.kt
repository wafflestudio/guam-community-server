package waffle.guam.community.command.scrap

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.scrap.PostScrapEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.PostScrapNotFound
import waffle.guam.community.service.command.scrap.DeletePostScrap
import waffle.guam.community.service.command.scrap.DeletePostScrapHandler

@DataJpaTest
class DeletePostScrapHandlerTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {
    private val deletePostScrapHandler = DeletePostScrapHandler(postRepository)
    val user = userRepository.save(UserEntity(immigrationId = 0L))
    val post = postRepository.save(PostEntity(boardId = 0L, user = user, title = "제목", content = "게시글"))

    @BeforeEach
    fun createPostScrap() {
        postRepository.save(
            post.apply { scraps.add(PostScrapEntity(this, user)) }
        )
    }

    @DisplayName("게시글 스크랩을 삭제할 수 있다.")
    @Test
    fun deletePostScrap() {
        // given @BeforeEach

        // when
        val command = DeletePostScrap(postId = post.id, userId = user.id)
        val result = deletePostScrapHandler.handle(command)

        // then
        assertThat(result.postId).isEqualTo(command.postId)
        assertThat(result.userId).isEqualTo(command.userId)

        val scraps = postRepository.getById(post.id).scraps
        assertThat(scraps).isEmpty()
    }

    @DisplayName("게시글이 존재하지 않으면 에러가 발생한다.")
    @Test
    fun postNotFound() {
        // given
        val user = userRepository.save(UserEntity(immigrationId = 0L))

        // when
        val command = DeletePostScrap(postId = 404L, userId = user.id)

        // then
        assertThrows<PostNotFound> {
            deletePostScrapHandler.handle(command)
        }
    }

    @DisplayName("스크랩한 유저가 아니면 에러가 발생한다.")
    @Test
    fun userNotFound() {
        // given
        val user = userRepository.save(UserEntity(immigrationId = 0L))
        val post = postRepository.save(PostEntity(boardId = 0L, user = user, title = "제목", content = "게시글"))

        // when
        val command = DeletePostScrap(postId = post.id, userId = 404L)

        // then
        assertThrows<PostScrapNotFound> {
            deletePostScrapHandler.handle(command)
        }
    }
}
