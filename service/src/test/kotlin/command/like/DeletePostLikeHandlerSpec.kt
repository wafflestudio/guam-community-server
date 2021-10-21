package waffle.guam.community.command.like

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostLikeNotFound
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.command.like.DeletePostLike
import waffle.guam.community.service.command.like.DeletePostLikeHandler

@Sql("classpath:/command/like/test.sql")
@DataJpaTest
@Transactional
class DeletePostLikeHandlerSpec(
    private val postRepository: PostRepository,
) : FeatureSpec() {
    private val handler = DeletePostLikeHandler(postRepository)
    private val command = DeletePostLike(postId = 2, userId = 1)

    init {
        feature("포스트 좋아요 취소 실패") {
            scenario("해당 포스트가 존재하지 않으면 에러가 발생한다.") {
                shouldThrowExactly<PostNotFound> {
                    handler.handle(command.copy(postId = 404L))
                }
            }

            scenario("해당 포스트에 좋아요를 누르지 않은 유저의 요청이면 에러가 발생한다.") {
                shouldThrowExactly<PostLikeNotFound> {
                    handler.handle(command.copy(userId = 3))
                }
            }
        }

        feature("포스트 좋아요 취소 성공") {
            scenario("요청이 유효하면 좋아요 취소에 성공한다.") {
                val result = handler.handle(command)
                val likeCanceledPost = postRepository.findByIdOrNull(command.postId)!!
                val userIdsWhoLikedPost = likeCanceledPost.likes.map { it.user.id }

                userIdsWhoLikedPost shouldNotContain command.userId
                result.run {
                    postId shouldBe command.postId
                    userId shouldBe command.userId
                }
            }
        }
    }
}
