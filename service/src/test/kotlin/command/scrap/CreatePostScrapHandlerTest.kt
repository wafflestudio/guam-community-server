package waffle.guam.community.command.scrap

import org.assertj.core.api.Assertions.assertThat
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
import waffle.guam.community.service.PostScrapConflict
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.scrap.CreatePostScrap
import waffle.guam.community.service.command.scrap.CreatePostScrapHandler

@DataJpaTest
class CreatePostScrapHandlerTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {
    private val createPostScrapHandler = CreatePostScrapHandler(postRepository, userRepository)

    @DisplayName("게시글을 스크랩 할 수 있다.")
    @Test
    fun createPostScrap() {
        // given
        val user = userRepository.save(UserEntity(immigrationId = 0L))
        val post = postRepository.save(PostEntity(boardId = 0L, user = user, title = "제목", content = "게시글"))

        // when
        val command = CreatePostScrap(postId = post.id, userId = user.id)
        val result = createPostScrapHandler.handle(command)

        // then
        assertThat(result.postId).isEqualTo(command.postId)
        assertThat(result.userId).isEqualTo(command.userId)

        val scraps = postRepository.getById(post.id).scraps
        assertThat(scraps).hasSize(1)
        assertThat(scraps.map { it.user.id }).contains(user.id)
    }

    @DisplayName("게시글이 존재하지 않으면 에러가 발생한다.")
    @Test
    fun postNotFound() {
        // given
        val user = userRepository.save(UserEntity(immigrationId = 0L))

        // when
        val command = CreatePostScrap(postId = 404L, userId = user.id)

        // then
        assertThrows<PostNotFound> {
            createPostScrapHandler.handle(command)
        }
    }

    @DisplayName("유저가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun userNotFound() {
        // given
        val user = userRepository.save(UserEntity(immigrationId = 0L))
        val post = postRepository.save(PostEntity(boardId = 0L, user = user, title = "제목", content = "게시글"))

        // when
        val command = CreatePostScrap(postId = post.id, userId = 404L)

        // then
        assertThrows<UserNotFound> {
            createPostScrapHandler.handle(command)
        }
    }

    @DisplayName("어떤 유저가 이미 스크랩을 했을 경우 에러가 발생한다.")
    @Test
    fun postScrapConflict() {
        // given
        val user = userRepository.save(UserEntity(immigrationId = 0L))
        val post = postRepository.save(
            PostEntity(boardId = 0L, user = user, title = "제목", content = "게시글")
                .apply { scraps.add(PostScrapEntity(this, user)) }
        )

        // when
        val command = CreatePostScrap(postId = post.id, userId = user.id)

        // then
        assertThrows<PostScrapConflict> {
            createPostScrapHandler.handle(command)
        }
    }
}
