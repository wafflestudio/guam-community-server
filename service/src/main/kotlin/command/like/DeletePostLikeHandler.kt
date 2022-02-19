package waffle.guam.community.service.command.like

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.PostLikeNotFound
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeletePostLikeHandler(
    private val postRepository: PostRepository,
) : CommandHandler<DeletePostLike, PostLikeDeleted> {
    @Transactional
    override fun handle(command: DeletePostLike): PostLikeDeleted {
        val (postId, userId) = command

        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFound(postId)

        post.removeLikeBy(userId)

        return PostLikeDeleted(postId = postId, userId = userId)
    }

    private fun PostEntity.removeLikeBy(userId: Long) {
        val targetLike = likes.find { it.user.id == userId } ?: throw PostLikeNotFound(postId = id, userId = userId)

        likes.remove(targetLike)
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
