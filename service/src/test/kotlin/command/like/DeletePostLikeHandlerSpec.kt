package waffle.guam.community.command.like

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.PostLikeNotFound
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.like.DeletePostLike
import waffle.guam.community.service.command.like.DeletePostLikeHandler

@Sql("classpath:/command/like/test.sql")
@DataJpaTest
@Transactional
class DeletePostLikeHandlerSpec @Autowired constructor(
    private val postRepository: PostRepository,
) {
    private val handler = DeletePostLikeHandler(postRepository)
    private val command = DeletePostLike(postId = 2, userId = 1)

    @DisplayName("해당 포스트가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun undoLikeNotExistingPost() {
        assertThrows<PostNotFound> {
            handler.handle(command.copy(postId = 404L))
        }
    }

    @DisplayName("해당 포스트에 좋아요를 누르지 않은 유저의 요청이면 에러가 발생한다.")
    @Test
    fun undoNotLikedPost() {
        assertThrows<PostLikeNotFound> {
            handler.handle(command.copy(userId = 3))
        }
    }

    @DisplayName("요청이 유효하면 좋아요 취소에 성공한다.")
    @Test
    fun undoLikeSuccessfully() {
        val result = handler.handle(command)
        val likeCanceledPost = postRepository.findByIdOrNull(command.postId)!!
        val userIdsWhoLikedPost = likeCanceledPost.likes.map { it.user.id }

        assertThat(command.userId).isNotIn(userIdsWhoLikedPost)
        assertThat(command.postId).isEqualTo(result.postId)
        assertThat(command.userId).isEqualTo(result.userId)
    }
}
