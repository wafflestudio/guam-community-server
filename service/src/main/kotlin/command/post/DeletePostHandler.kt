package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import java.time.Instant

@Service
class DeletePostHandler(
    private val postRepository: PostRepository,
) : CommandHandler<DeletePost, PostDeleted>() {
    override fun canHandle(command: Command): Boolean = command is DeletePost

    override fun internalHandle(command: DeletePost): PostDeleted {
        val post = postRepository.findByIdOrNull(command.postId) ?: throw Exception()

        if (post.userId != command.userId) {
            throw Exception()
        }

        post.status = PostEntity.Status.DELETED
        post.updatedAt = Instant.now()

        return PostDeleted(
            postId = post.id,
            boardId = post.boardId,
            userId = post.userId
        )
    }
}

data class DeletePost(
    val postId: Long,
    val userId: Long,
) : Command

data class PostDeleted(
    val postId: Long,
    val boardId: Long,
    val userId: Long,
) : Result
