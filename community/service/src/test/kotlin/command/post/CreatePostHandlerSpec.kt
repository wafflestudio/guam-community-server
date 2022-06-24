package waffle.guam.community.command.post

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import waffle.guam.community.data.jdbc.board.BoardRepository
import waffle.guam.community.data.jdbc.category.CategoryRepository
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.BadCategoryId
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.command.post.CreatePost
import waffle.guam.community.service.command.post.CreatePostHandler

@Sql("classpath:/command/post/test.sql")
@DataJpaTest
class CreatePostHandlerSpec @Autowired constructor(
    private val postRepository: PostRepository,
    categoryRepository: CategoryRepository,
    boardRepository: BoardRepository,
    mockImageHandler: UploadImageListHandler,
) {
    private val handler = CreatePostHandler(postRepository, boardRepository, categoryRepository, mockImageHandler)
    private val command = CreatePost(
        boardId = 1L,
        userId = 2L,
        title = "Test Post",
        content = "This is Post Test",
        images = emptyList(),
        categoryId = 2L
    )

    @DisplayName("해당 카테고리가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun notExistingCategory() {
        assertThrows<BadCategoryId> {
            handler.handle(command.copy(categoryId = 404L))
        }
    }

    @DisplayName("요청이 유효하면 성공적으로 생성한다.")
    @Test
    fun createSuccessfully() {
        val result = handler.handle(command)
        val createdPost = postRepository.findByIdOrNull(result.postId)

        assertThat(createdPost).isNotEqualTo(null)
        assertThat(command.boardId).isEqualTo(createdPost!!.boardId)
        assertThat(command.userId).isEqualTo(createdPost.userId)
        assertThat(command.categoryId).isEqualTo(createdPost.categories.first().category.id)
        assertThat(command.title).isEqualTo(createdPost.title)
        assertThat(command.content).isEqualTo(createdPost.content)
    }
}
