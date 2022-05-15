package waffle.guam.community.service.command.like

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.GuamConflict
import waffle.guam.community.common.PostCommentNotFound
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.comment.PostCommentQueryGenerator
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.data.jdbc.like.PostCommentLikeEntity
import waffle.guam.community.data.jdbc.push.PushEventEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.PushEventResult

@Service
class CreatePostCommentLikeHandler(
    private val postCommentRepository: PostCommentRepository,
) : CommandHandler<CreatePostCommentLike, PostCommentLikeCreated>, PostCommentQueryGenerator {

    @Transactional
    override fun handle(command: CreatePostCommentLike): PostCommentLikeCreated {
        val (postId, commentId, userId) = command

        val comment = postCommentRepository.findOne(commentId(commentId) * fetchCommentLikes())
            ?: throw PostCommentNotFound(commentId)

        comment.addLikeBy(userId)

        return PostCommentLikeCreated(
            postId = postId,
            commentId = comment.id,
            userId = comment.user.id,
            content = comment.content,
            isAnonymous = comment.post.isAnonymous,
            consumingUserId = comment.post.user.id,
        )
    }

    private fun PostCommentEntity.addLikeBy(userId: Long) {
        if (likes.map { it.userId }.contains(userId)) {
            throw GuamConflict("USER $userId ALREADY LIKED COMMENT $id")
        }

        likes.add(PostCommentLikeEntity(comment = this, userId = userId))
    }
}

data class CreatePostCommentLike(
    val postId: Long,
    val commentId: Long,
    val userId: Long,
) : Command

data class PostCommentLikeCreated(
    val postId: Long,
    val commentId: Long,
    val userId: Long,
    override val consumingUserId: Long,
    @get:JsonIgnore val content: String,
    @get:JsonIgnore val isAnonymous: Boolean,
) : PushEventResult {
    override val producedUserId: Long
        get() = userId

    override fun toPushEventEntities(producedBy: UserEntity): List<PushEventEntity> {
        return PushEventEntity(
            userId = consumingUserId,
            writer = producedBy,
            kind = PushEventEntity.Kind.POST_COMMENT_LIKE,
            body = content.take(50),
            linkUrl = "/api/v1/posts/$postId/comments",
            isAnonymousEvent = isAnonymous,
        ).let(::listOf)
    }
}
