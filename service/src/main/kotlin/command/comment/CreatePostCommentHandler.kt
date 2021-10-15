package waffle.guam.community.service.command.comment

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreatePostCommentHandler(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : CommandHandler<CreatePostComment, PostCommentCreated> {

    @Transactional
    override fun handle(command: CreatePostComment): PostCommentCreated {
        val (postId, userId, content) = command

        val post = postRepository.findByIdOrNull(postId) ?: throw Exception("POST NOT FOUND $postId")

        post.addCommentBy(userId, content)

        return PostCommentCreated(postId = postId, userId = userId)
    }

    private fun PostEntity.addCommentBy(userId: Long, content: String) {
        val user = userRepository.findByIdOrNull(userId) ?: throw Exception("USER NOT FOUND $userId")

        comments.add(PostCommentEntity(post = this, user = user, content = content))
    }
}

data class CreatePostComment(
    val postId: Long,
    val userId: Long,
    val content: String,
) : Command

data class PostCommentCreated(
    val postId: Long,
    val userId: Long,
) : Result
