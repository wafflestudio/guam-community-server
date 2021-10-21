package waffle.guam.community.service.command.like

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.like.PostLikeEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.PostLikeConflict
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreatePostLikeHandler(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : CommandHandler<CreatePostLike, PostLikeCreated> {

    @Transactional
    override fun handle(command: CreatePostLike): PostLikeCreated {
        val (postId, userId) = command

        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFound(postId)

        post.addLikeBy(userId)

        return PostLikeCreated(postId = post.id, userId = command.userId)
    }

    private fun PostEntity.addLikeBy(userId: Long) {
        if (likes.map { it.user.id }.contains(userId)) {
            throw PostLikeConflict(postId = id, userId = userId)
        }

        likes.add(
            PostLikeEntity(
                post = this,
                user = userRepository.findByIdOrNull(userId) ?: throw UserNotFound(userId)
            )
        )
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
