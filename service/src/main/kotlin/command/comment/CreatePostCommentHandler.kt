package waffle.guam.community.service.command.comment

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
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
    private val userRepository: UserRepository
) : CommandHandler<CreatePostComment, PostCommentCreated>() {
    override fun canHandle(command: Command): Boolean = command is CreatePostComment

    override fun internalHandle(command: CreatePostComment): PostCommentCreated {
        val post = postRepository.findByIdOrNull(command.postId) ?: throw Exception()

        post.addCommentBy(command.userId, command.content)

        return PostCommentCreated(
            postId = post.id,
            userId = command.userId
        )
    }

    private fun PostEntity.addCommentBy(userId: Long, content: String) {
        val user = userRepository.findByIdOrNull(userId) ?: throw Exception()

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
