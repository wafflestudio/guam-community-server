package waffle.guam.favorite.service.command

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.CommentLikeEntity
import waffle.guam.favorite.data.r2dbc.CommentLikeRepository
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.infra.Comment
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.model.CommentLike
import waffle.guam.favorite.service.saga.CommentLikeSaga
import java.time.Instant

@Service
class CommentLikeCreateHandler(
    override val commentLikeRepository: CommentLikeRepository,
    override val commentLikeSaga: CommentLikeSaga,
    private val community: CommunityService,
) : CommentLikeCommandHandler() {

    override suspend fun internalHandle(commentLike: CommentLike): CommentLikeEvent = coroutineScope {
        val comment = async {
            community.getComment(commentLike.postCommentId) ?: throw RuntimeException("Valid Comment Not Found.")
        }

        if (commentLike.exists()) {
            throw DuplicateCommentLikeException()
        }

        commentLikeRepository.save(
            CommentLikeEntity(
                postCommentId = commentLike.postCommentId,
                userId = commentLike.userId
            )
        )

        CommentLikeCreated(commentLike = commentLike, comment = comment.await())
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

sealed class CommentLikeEvent(override val eventTime: Instant = Instant.now()) : Event
data class CommentLikeCreated(val commentLike: CommentLike, val comment: Comment) : CommentLikeEvent()
data class CommentLikeDeleted(val commentLike: CommentLike) : CommentLikeEvent()

class DuplicateCommentLikeException(
    override val status: Int = 409,
    override val msg: String = "이미 좋아요를 누른 댓글입니다.",
) : ServiceError()

class CommentLikeNotFoundException(
    override val status: Int = 404,
    override val msg: String = "좋아요를 누르지 않은 댓글입니다.",
) : ServiceError()
