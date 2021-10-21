package waffle.guam.community.command.post

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.TagRepository
import waffle.guam.community.service.InvalidArgumentException
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UnAuthorized
import waffle.guam.community.service.command.post.UpdatePost
import waffle.guam.community.service.command.post.UpdatePostHandler

@Sql("classpath:/command/post/test.sql")
@DataJpaTest
@Transactional
class UpdatePostHandlerSpec(
    private val postRepository: PostRepository,
    tagRepository: TagRepository,
) : FeatureSpec() {
    private val handler = UpdatePostHandler(postRepository, tagRepository)
    private val command = UpdatePost(
        postId = 1L,
        userId = 1L,
        title = "Update Test",
        content = "This is update test",
        tagId = 2L
    )

    init {
        feature("포스트 업데이트 실패") {
            scenario("해당 포스트가 존재하지 않으면 에러가 발생한다.") {
                shouldThrowExactly<PostNotFound> {
                    handler.handle(command.copy(postId = 404L))
                }
            }

            scenario("요청자가 해당 포스트의 작성자가 아니면 에러가 발생한다.") {
                shouldThrowExactly<UnAuthorized> {
                    handler.handle(command.copy(userId = 401L))
                }
            }

            scenario("요청의 모든 값이 null이면 에러가 발생한다.") {
                shouldThrowExactly<InvalidArgumentException> {
                    handler.handle(command.copy(title = null, content = null, tagId = null))
                }
            }
        }

        feature("포스트 업데이트 성공") {
            scenario("요청이 유효하면 성공적으로 업데이한다.") {
                val result = handler.handle(command)
                val updatedPost = postRepository.findByIdOrNull(command.postId)

                updatedPost shouldNotBe null
                updatedPost!!.run {
                    user.id shouldBe command.userId
                    title shouldBe command.title
                    content shouldBe command.content
                    tags.map { it.tag }[0].id shouldBe command.tagId
                }
                result.run {
                    postId shouldBe updatedPost.id
                    boardId shouldBe updatedPost.boardId
                    userId shouldBe updatedPost.user.id
                }
            }

            scenario("파라미터가 null이 아닌 값들만 성공적으로 업데이트한다.") {
                val partialNullCommand = command.copy(
                    title = "This is Update Test2",
                    content = null,
                    tagId = null
                )
                val oldPost = postRepository.findByIdOrNull(partialNullCommand.postId)!!
                val result = handler.handle(partialNullCommand)
                val updatedPost = postRepository.findByIdOrNull(command.postId)

                updatedPost shouldNotBe null
                updatedPost!!.run {
                    user.id shouldBe partialNullCommand.userId
                    title shouldBe partialNullCommand.title
                    content shouldBe oldPost.content
                    tags.map { it.tag }[0].id shouldBe oldPost.tags.map { it.tag }[0].id
                }
                result.run {
                    postId shouldBe updatedPost.id
                    boardId shouldBe updatedPost.boardId
                    userId shouldBe updatedPost.user.id
                }
            }
        }
    }
}
