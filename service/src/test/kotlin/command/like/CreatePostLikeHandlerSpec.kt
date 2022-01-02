package waffle.guam.community.command.like

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.PostLikeConflict
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.like.CreatePostLike
import waffle.guam.community.service.command.like.CreatePostLikeHandler

@Sql("classpath:/command/like/test.sql")
@DataJpaTest
@Transactional
class CreatePostLikeHandlerSpec @Autowired constructor(
    private val postRepository: PostRepository,
    userRepository: UserRepository
) {
    val handler = CreatePostLikeHandler(postRepository, userRepository)
    val command = CreatePostLike(postId = 1L, userId = 1L)

    @DisplayName("해당 포스트가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun likeNotExistingPost() {
        assertThrows<PostNotFound> {
            handler.handle(command.copy(postId = 404L))
        }
    }

    @DisplayName("해당 유저가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun likeByNotExistingUser() {
        assertThrows<UserNotFound> {
            handler.handle(command.copy(userId = 404L))
        }
    }

    @DisplayName("이미 좋아요를 누른 포스트라면 에러가 발생한다.")
    @Test
    fun likeAlreadyLikedPost() {
        assertThrows<PostLikeConflict> {
            handler.handle(command.copy(postId = 2L))
        }
    }

    @DisplayName("요청이 유효하면 좋아요에 성공한다.")
    @Test
    fun likeSuccessfully() {
        val result = handler.handle(command)
        val likedPost = postRepository.findByIdOrNull(command.postId)!!
        val userIdsWhoLikedPost = likedPost.likes.map { it.user.id }

        Assertions.assertThat(command.userId).isIn(userIdsWhoLikedPost)
        Assertions.assertThat(command.postId).isEqualTo(result.postId)
        Assertions.assertThat(command.userId).isEqualTo(result.userId)
    }
}
