package waffle.guam.community.service.command.like

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.PostCommentNotFound
import waffle.guam.community.common.PostLikeNotFound
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.comment.PostCommentQueryGenerator
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeletePostCommentLikeHandler(
    private val postCommentRepository: PostCommentRepository,
) : CommandHandler<DeletePostCommentLike, PostCommentLikeDeleted>, PostCommentQueryGenerator {

    @Transactional
    override fun handle(command: DeletePostCommentLike): PostCommentLikeDeleted {
        val (postId, commentId, userId) = command

        val comment = postCommentRepository.findOne(commentId(commentId) * fetchCommentLikes())
            ?: throw PostCommentNotFound(commentId)

        comment.deleteLikeBy(userId)

        return PostCommentLikeDeleted(
            postId = comment.post.id,
            commentId = comment.id,
            userId = comment.user.id
        )
    }

    private fun PostCommentEntity.deleteLikeBy(userId: Long) {
        val targetLike = likes.find { it.userId == userId } ?: throw PostLikeNotFound()

        likes.remove(targetLike)
    }
}

data class DeletePostCommentLike(
    val postId: Long,
    val commentId: Long,
    val userId: Long,
) : Command

data class PostCommentLikeDeleted(
    val postId: Long,
    val commentId: Long,
    val userId: Long,
) : Result
