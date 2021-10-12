package waffle.guam.community.service.command.comment

import org.springframework.stereotype.Component
import waffle.guam.community.data.jdbc.comment.CommentEntity
import waffle.guam.community.data.jdbc.comment.CommentRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler

@Component
class CreateCommentHandler(
    private val commentRepository: CommentRepository,
) : CommandHandler<CreateComment, CommentCreated>() {
    override fun internalHandle(command: CreateComment): CommentCreated =
        CommentCreated(
            id = commentRepository.save(CommentEntity(content = command.content)).id
        )

    override fun canHandle(command: Command): Boolean =
        command is CreateComment
}

data class CreateComment(
    val content: String
) : CommentCommand

data class CommentCreated(
    val id: Long
) : CommentResult
