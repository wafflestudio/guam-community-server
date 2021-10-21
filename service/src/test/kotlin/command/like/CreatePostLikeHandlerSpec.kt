package waffle.guam.community.command.like

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
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
class CreatePostLikeHandlerSpec(
    private val postRepository: PostRepository,
    userRepository: UserRepository
) : FeatureSpec() {
    val handler = CreatePostLikeHandler(postRepository, userRepository)
    val command = CreatePostLike(postId = 1L, userId = 1L)

    init {
        feature("포스트 좋아요 실패") {
            scenario("해당 포스트가 존재하지 않으면 에러가 발생한다.") {
                shouldThrowExactly<PostNotFound> {
                    handler.handle(command.copy(postId = 404L))
                }
            }

            scenario("해당 유저가 존재하지 않으면 에러가 발생한다.") {
                shouldThrowExactly<UserNotFound> {
                    handler.handle(command.copy(userId = 404L))
                }
            }

            scenario("이미 좋아요를 누른 포스트라면 에러가 발생한다.") {
                shouldThrowExactly<PostLikeConflict> {
                    handler.handle(command.copy(postId = 2L))
                }
            }
        }

        feature("포스트 좋아요 성공") {
            scenario("요청이 유효하면 좋아요에 성공한다.") {
                val result = handler.handle(command)
                val likedPost = postRepository.findByIdOrNull(command.postId)!!
                val userIdsWhoLikedPost = likedPost.likes.map { it.user.id }

                userIdsWhoLikedPost shouldContain command.userId
                result.run {
                    postId shouldBe command.postId
                    userId shouldBe command.userId
                }
            }
        }
    }
}
