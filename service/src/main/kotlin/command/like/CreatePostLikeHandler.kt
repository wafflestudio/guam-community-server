package waffle.guam.community.service.command.like

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.PostLikeConflict
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.common.UserNotFound
import waffle.guam.community.data.jdbc.like.PostLikeEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.push.PushEventEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.PushEventResult

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

        return PostLikeCreated(
            postId = post.id,
            userId = command.userId,
            postUserId = post.user.id,
            content = post.content,
            isAnonymous = post.isAnonymous,
        )
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
    @get:JsonIgnore val postUserId: Long,
    @get:JsonIgnore val content: String,
    @get:JsonIgnore val isAnonymous: Boolean,
) : PushEventResult {
    override val consumingUserId: Long
        get() = postUserId

    override val producedUserId: Long
        get() = userId

    override fun toPushEventEntities(producedBy: UserEntity): List<PushEventEntity> {
        return PushEventEntity(
            userId = consumingUserId,
            writer = producedBy,
            kind = PushEventEntity.Kind.POST_LIKE,
            body = content.take(50),
            linkUrl = "/api/v1/posts/$postId",
            isAnonymousEvent = isAnonymous,
        ).let(::listOf)
    }
}
