package waffle.guam.community.service.command.like

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.comment.PostCommentQueryGenerator
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.data.jdbc.like.PostCommentLikeEntity
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreatePostCommentLikeHandler(
    private val postCommentRepository: PostCommentRepository,
) : CommandHandler<CreatePostCommentLike, PostCommentLikeCreated>, PostCommentQueryGenerator {
    override fun handle(command: CreatePostCommentLike): PostCommentLikeCreated {
        val (postId, commentId, userId) = command

        val comment = postCommentRepository.findOne(commentId(commentId) * fetchCommentLikes())
            ?: throw Exception("COMMENT NOT FOUND $commentId")

        comment.addLikeBy(userId)

        return PostCommentLikeCreated(
            postId = comment.post.id,
            commentId = comment.id,
            userId = comment.user.id
        )
    }

    private fun PostCommentEntity.addLikeBy(userId: Long) {
        if (likes.map { it.userId }.contains(userId)) {
            throw Exception("USER $userId ALREADY LIKED COMMENT $id")
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
) : Result
