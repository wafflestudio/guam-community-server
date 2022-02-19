package waffle.guam.community.command.post

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.TagNotFound
import waffle.guam.community.common.UserNotFound
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.TagRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.image.ImageListUploaded
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.command.post.CreatePost
import waffle.guam.community.service.command.post.CreatePostHandler

@Sql("classpath:/command/post/test.sql")
@DataJpaTest
@Transactional
class CreatePostHandlerSpec @Autowired constructor(
    private val postRepository: PostRepository,
    tagRepository: TagRepository,
    userRepository: UserRepository,
) {
    private val mockImageHandler: UploadImageListHandler = mockk()
    private val handler = CreatePostHandler(postRepository, tagRepository, userRepository, mockImageHandler)
    private val command = CreatePost(
        boardId = 1L,
        userId = 2L,
        title = "Test Post",
        content = "This is Post Test",
        images = emptyList(),
        tagId = 2L
    )

    init {
        val imageCommandSlot = slot<UploadImageList>()

        every {
            mockImageHandler.handle(capture(imageCommandSlot))
        } answers {
            val captured = imageCommandSlot.captured
            ImageListUploaded(captured.images.mapIndexed { i, _ -> "TEST/$i" })
        }
    }

    @DisplayName("해당 유저가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun notExistingUser() {
        assertThrows<UserNotFound> {
            handler.handle(command.copy(userId = 404L))
        }
    }

    @DisplayName("해당 태그가 존재하지 않으면 에러가 발생한다.")
    @Test
    fun notExistingTag() {
        assertThrows<TagNotFound> {
            handler.handle(command.copy(tagId = 404L))
        }
    }

    @DisplayName("요청이 유효하면 성공적으로 생성한다.")
    @Test
    fun createSuccessfully() {
        val result = handler.handle(command)
        val createdPost = postRepository.findByIdOrNull(result.postId)

        assertThat(createdPost).isNotEqualTo(null)
        assertThat(command.boardId).isEqualTo(createdPost!!.boardId)
        assertThat(command.userId).isEqualTo(createdPost.user.id)
        assertThat(command.tagId).isEqualTo(createdPost.tags.first().tag.id)
        assertThat(command.title).isEqualTo(createdPost.title)
        assertThat(command.content).isEqualTo(createdPost.content)
    }
}
