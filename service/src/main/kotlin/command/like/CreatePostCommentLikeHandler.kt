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
import waffle.guam.community.service.command.Result

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
    @get:JsonIgnore val content: String,
    @get:JsonIgnore val isAnonymous: Boolean,
) : Result {
    fun toPushEventEntity(writer: UserEntity): PushEventEntity {
        return PushEventEntity(
            userId = userId,
            writer = writer,
            kind = PushEventEntity.Kind.POST_COMMENT_LIKE,
            body = content.take(50),
            linkUrl = "/api/v1/posts/$postId/comments",
            isAnonymousEvent = isAnonymous,
        )
    }
}
