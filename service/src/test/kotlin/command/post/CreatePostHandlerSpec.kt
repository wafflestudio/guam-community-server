package waffle.guam.community.command.post

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.TagRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.TagNotFound
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.image.ImageListUploaded
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.command.post.CreatePost
import waffle.guam.community.service.command.post.CreatePostHandler

@Sql("classpath:/command/post/test.sql")
@DataJpaTest
@Transactional
class CreatePostHandlerSpec(
    private val postRepository: PostRepository,
    tagRepository: TagRepository,
    userRepository: UserRepository,
) : FeatureSpec() {
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

        feature("포스트 생성 실패") {
            scenario("해당 유저가 존재하지 않으면 에러가 발생한다.") {
                shouldThrowExactly<UserNotFound> {
                    handler.handle(command.copy(userId = 404L))
                }
            }

            scenario("해당 태그가 존재하지 않으면 에러가 발생한다.") {
                shouldThrowExactly<TagNotFound> {
                    handler.handle(command.copy(tagId = 404L))
                }
            }
        }

        feature("포스트 생성 성공") {
            scenario("요청이 유효하면 성공적으로 생성한다.") {
                val result = handler.handle(command)
                val createdPost = postRepository.findByIdOrNull(result.postId)

                createdPost shouldNotBe null
                createdPost!!.run {
                    boardId shouldBe command.boardId
                    user.id shouldBe command.userId
                    tags.map { it.tag.id }[0] shouldBe command.tagId
                    title shouldBe command.title
                    content shouldBe command.content
                }
            }
        }
    }
}
