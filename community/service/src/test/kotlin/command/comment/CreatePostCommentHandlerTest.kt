package waffle.guam.community.command.comment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.comment.CreatePostComment
import waffle.guam.community.service.command.comment.CreatePostCommentHandler
import waffle.guam.community.service.command.comment.PostCommentCreated
import waffle.guam.community.service.command.image.UploadImageListHandler

@Sql("classpath:command/post/test.sql")
@DataJpaTest
internal class CreatePostCommentHandlerTest @Autowired constructor(
    private val postRepository: PostRepository,
    mockImageHandler: UploadImageListHandler,
) {
    private val sut = CreatePostCommentHandler(postRepository, mockImageHandler)
    private val command = CreatePostComment(
        postId = 1L,
        userId = 1L,
        content = "this is test",
        images = emptyList(),
        mentionIds = listOf(1, 2, 3),
    )

    @DisplayName("게시글에 댓글을 작성할 수 있다.")
    @Test
    fun postComment() {
        // given test.sql

        // when
        val result = sut.handle(command)
        val post = postRepository.getById(result.postId)

        // then
        assertThat(post.comments).hasSize(1)
        assertThat(result).isEqualTo(
            PostCommentCreated(
                postId = 1L,
                postUserId = 1L,
                content = "this is test",
                isAnonymous = true,
                writerId = 1L,
                mentionIds = listOf(1, 2, 3),
            )
        )
    }
}
