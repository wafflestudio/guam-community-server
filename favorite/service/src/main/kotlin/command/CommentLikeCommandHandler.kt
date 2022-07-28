package waffle.guam.favorite.service.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.CommentLikeEntity
import waffle.guam.favorite.data.r2dbc.CommentLikeRepository
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.model.CommentLike
import waffle.guam.favorite.service.saga.CommentLikeSaga
import java.time.Instant

@Service
class CommentLikeCreateHandler(
    override val commentLikeRepository: CommentLikeRepository,
    override val commentLikeSaga: CommentLikeSaga,
) : CommentLikeCommandHandler() {

    override suspend fun internalHandle(commentLike: CommentLike): CommentLikeEvent {
        if (commentLike.exists()) {
            throw DuplicateCommentLikeException()
        }

        commentLikeRepository.save(
            CommentLikeEntity(
                postCommentId = commentLike.postCommentId,
                userId = commentLike.userId
            )
        )

        return CommentLikeCreated(commentLike)
    }
}

@Service
class CommentLikeDeleteHandler(
    override val commentLikeRepository: CommentLikeRepository,
    override val commentLikeSaga: CommentLikeSaga,
) : CommentLikeCommandHandler() {

    override suspend fun internalHandle(commentLike: CommentLike): CommentLikeEvent {
        val updatedRows = commentLikeRepository.deleteByPostCommentIdAndUserId(
            postCommentId = commentLike.postCommentId,
            userId = commentLike.userId
        )

        if (updatedRows < 1) {
            throw CommentLikeNotFoundException()
        }

        return CommentLikeDeleted(commentLike)
    }
}

abstract class CommentLikeCommandHandler : CommandHandler<CommentLike, CommentLikeEvent> {
    abstract val commentLikeRepository: CommentLikeRepository
    abstract val commentLikeSaga: CommentLikeSaga

    @Transactional
    override suspend fun handle(command: CommentLike): CommentLikeEvent {
        // TODO: 더 느슨하게 불가능..?
        return internalHandle(command).also { commentLikeSaga.handleEvent(it) }
    }

    protected suspend fun CommentLike.exists(): Boolean {
        return commentLikeRepository.existsByPostCommentIdAndUserId(postCommentId = postCommentId, userId = userId)
    }

    protected abstract suspend fun internalHandle(commentLike: CommentLike): CommentLikeEvent
}

sealed class CommentLikeEvent(override val eventTime: Instant = Instant.now()) : Event {
    abstract val commentLike: CommentLike
}

data class CommentLikeCreated(override val commentLike: CommentLike) : CommentLikeEvent()
data class CommentLikeDeleted(override val commentLike: CommentLike) : CommentLikeEvent()

class DuplicateCommentLikeException(
    override val status: Int = 409,
    override val msg: String = "이미 좋아요를 누른 댓글입니다.",
) : ServiceError()

class CommentLikeNotFoundException(
    override val status: Int = 404,
    override val msg: String = "좋아요를 누르지 않은 댓글입니다.",
) : ServiceError()
