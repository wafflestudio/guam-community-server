package waffle.guam.community.service.command.scrap

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.common.PostScrapConflict
import waffle.guam.community.common.UserNotFound
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.scrap.PostScrapEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreatePostScrapHandler(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : CommandHandler<CreatePostScrap, PostScrapCreated> {
    @Transactional
    override fun handle(command: CreatePostScrap): PostScrapCreated {
        val post = postRepository.findByIdOrNull(command.postId) ?: throw PostNotFound(command.postId)

        post.addScrapBy(command.userId)
        return PostScrapCreated(postId = command.postId, userId = command.userId)
    }

    private fun PostEntity.addScrapBy(userId: UserId) {
        if (scraps.map { it.user.id }.contains(userId)) {
            throw PostScrapConflict(postId = this.id, userId = userId)
        }

        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFound(userId)
        scraps.add(PostScrapEntity(post = this, user = user))
    }
}

data class CreatePostScrap(
    val postId: PostId,
    val userId: UserId,
) : Command

data class PostScrapCreated(
    val postId: PostId,
    val userId: UserId,
) : Result
