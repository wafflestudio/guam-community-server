package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UnAuthorized
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeletePostHandler(
    private val postRepository: PostRepository,
) : CommandHandler<DeletePost, PostDeleted> {

    @Transactional
    override fun handle(command: DeletePost): PostDeleted {
        val (postId, userId) = command

        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFound("POST NOT FOUND $postId")

        post.deleteBy(userId)

        return PostDeleted(postId = post.id, boardId = post.boardId, userId = post.user.id)
    }

    private fun PostEntity.deleteBy(userId: Long) {
        if (this.user.id != userId) {
            throw UnAuthorized("USER $userId NOT AUTHORIZED TO POST $id")
        }

        status = PostEntity.Status.DELETED
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
