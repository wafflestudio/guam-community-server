package waffle.guam.community.service.command.comment

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.post.fetchComments
import waffle.guam.community.data.jdbc.post.postId
import waffle.guam.community.data.jdbc.times
import waffle.guam.community.service.Forbidden
import waffle.guam.community.service.PostCommentNotFound
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class UpdatePostCommentHandler(
    private val postRepository: PostRepository,
) : CommandHandler<UpdatePostComment, PostCommentUpdated> {

    @Transactional
    override fun handle(command: UpdatePostComment): PostCommentUpdated {
        val (postId, userId, commentId, content) = command

        val post = postRepository.findOne(postId(postId) * fetchComments())
            ?: throw PostNotFound(postId)

        val targetComment = post.comments.find { it.id == commentId }
            ?: throw PostCommentNotFound(commentId)

        targetComment.updateBy(userId, content)

        return PostCommentUpdated(postId = postId, userId = userId, commentId = commentId)
    }

    private fun PostCommentEntity.updateBy(userId: Long, content: String) {
        if (this.userId != userId) {
            throw Forbidden("USER NOT AUTHORIZED TO UPDATE COMMENT $id")
        }

        this.content = content
    }
}

data class UpdatePostComment(
    val postId: Long,
    val userId: Long,
    val commentId: Long,
    val content: String,
) : Command

data class PostCommentUpdated(
    val postId: Long,
    val userId: Long,
    val commentId: Long,
) : Result
