package waffle.guam.community.service.command.scrap

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.PostScrapNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeletePostScrapHandler(
    private val postRepository: PostRepository,
) : CommandHandler<DeletePostScrap, PostScrapDeleted> {
    @Transactional
    override fun handle(command: DeletePostScrap): PostScrapDeleted {
        val post = postRepository.findByIdOrNull(command.postId) ?: throw PostNotFound(command.postId)

        post.removeScrapBy(command.userId)
        return PostScrapDeleted(postId = command.postId, userId = command.userId)
    }

    private fun PostEntity.removeScrapBy(userId: UserId) {
        val targetScrap = scraps.find { it.user.id == userId } ?: throw PostScrapNotFound(this.id, userId)
        scraps.remove(targetScrap)
    }
}

data class DeletePostScrap(
    val postId: PostId,
    val userId: UserId,
) : Command

data class PostScrapDeleted(
    val postId: PostId,
    val userId: UserId,
) : Result
