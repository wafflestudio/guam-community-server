package waffle.guam.community.service.command.like

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.like.PostLikeEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreatePostLikeHandler(
    private val postRepository: PostRepository,
) : CommandHandler<CreatePostLike, PostLikeCreated>() {
    override fun canHandle(command: Command): Boolean = command is CreatePostLike

    override fun internalHandle(command: CreatePostLike): PostLikeCreated {
        val post = postRepository.findByIdOrNull(command.postId) ?: throw Exception()

        post.addLikeBy(command.userId)

        return PostLikeCreated(postId = post.id, userId = command.userId)
    }

    private fun PostEntity.addLikeBy(userId: Long) {
        if (likes.map { it.userId }.contains(userId)) {
            throw Exception()
        }

        likes.add(PostLikeEntity(post = this, userId = userId))
    }
}

data class CreatePostLike(
    val postId: Long,
    val userId: Long,
) : Command

data class PostLikeCreated(
    val postId: Long,
    val userId: Long,
) : Result
