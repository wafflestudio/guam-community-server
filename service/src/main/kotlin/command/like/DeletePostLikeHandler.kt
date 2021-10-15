package waffle.guam.community.service.command.like

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeletePostLikeHandler(
    private val postRepository: PostRepository,
) : CommandHandler<DeletePostLike, PostLikeDeleted>() {
    override fun canHandle(command: Command): Boolean = command is DeletePostLike

    override fun internalHandle(command: DeletePostLike): PostLikeDeleted {
        val post = postRepository.findByIdOrNull(command.postId) ?: throw Exception()

        post.removeLikeBy(command.userId)

        return PostLikeDeleted(postId = post.id, userId = command.userId)
    }

    private fun PostEntity.removeLikeBy(userId: Long) {
        likes.find { it.userId == userId }?.let { likes.remove(it) } ?: throw Exception()
    }
}

data class DeletePostLike(
    val postId: Long,
    val userId: Long,
) : Command

data class PostLikeDeleted(
    val postId: Long,
    val userId: Long,
) : Result
