package waffle.guam.community.command.post

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.Forbidden
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.command.post.DeletePost
import waffle.guam.community.service.command.post.DeletePostHandler

@Sql("classpath:/command/post/test.sql")
@DataJpaTest
@Transactional
class DeletePostHandlerSpec(
    private val postRepository: PostRepository,
) : FeatureSpec() {
    private val handler = DeletePostHandler(postRepository)
    private val command = DeletePost(postId = 1, userId = 1)

    init {
        feature("포스트 삭제 실패") {
            scenario("해당 포스트가 존재하지 않으면 에러가 발생한다.") {
                shouldThrowExactly<PostNotFound> {
                    handler.handle(command.copy(postId = 404L))
                }
            }

            scenario("요청자가 해당 포스트의 작성자가 아니면 에러가 발생한다.") {
                shouldThrowExactly<Forbidden> {
                    handler.handle(command.copy(userId = 401L))
                }
            }
        }

        feature("포스트 삭제 성공") {
            scenario("요청이 유효하면 성공적으로 삭제한다.") {
                val result = handler.handle(command)
                val deletedPost = postRepository.findByIdOrNull(command.postId)

                deletedPost shouldNotBe null
                deletedPost!!.status shouldBe PostEntity.Status.DELETED
                result.run {
                    postId shouldBe deletedPost.id
                    boardId shouldBe deletedPost.boardId
                    userId shouldBe deletedPost.user.id
                }
            }
        }
    }
}
