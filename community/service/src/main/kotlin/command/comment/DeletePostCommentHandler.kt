package waffle.guam.community.service.command.comment

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.Forbidden
import waffle.guam.community.service.PostCommentNotFound
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeletePostCommentHandler(
    private val postRepository: PostRepository,
) : CommandHandler<DeletePostComment, PostCommentDeleted>, PostQueryGenerator {

    @Transactional
    override fun handle(command: DeletePostComment): PostCommentDeleted {
        val (postId, commentId, userId) = command

        val post = postRepository.findOne(spec = postId(postId) * fetchComments())
            ?: throw PostNotFound(postId)

        post.deleteCommentBy(commentId, userId)

        return PostCommentDeleted(postId = postId, commentId = commentId, userId = userId)
    }

    private fun PostEntity.deleteCommentBy(commentId: Long, userId: Long) {
        val targetComment = comments.find { it.id == commentId }
            ?: throw PostCommentNotFound(commentId)

        if (targetComment.userId != userId) {
            throw Forbidden("USER $userId NOT AUTHORIZED TO COMMENT $commentId")
        }

        targetComment.status = PostCommentEntity.Status.DELETED
    }
}

data class DeletePostComment(
    val postId: Long,
    val commentId: Long,
    val userId: Long,
) : Command

data class PostCommentDeleted(
    val postId: Long,
    val commentId: Long,
    val userId: Long,
) : Result
