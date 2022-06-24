package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.CommentLikeCreated
import waffle.guam.favorite.service.command.CommentLikeDeleted
import waffle.guam.favorite.service.command.CommentLikeEvent
import waffle.guam.favorite.service.infra.NotificationService
import waffle.guam.favorite.service.query.CommentLikeCountStore

interface CommentLikeSaga {
    suspend fun handleEvent(event: CommentLikeEvent)
}

@Service
class CommentLikeSagaImpl(
    private val commentLikeCountStore: CommentLikeCountStore.Mutable,
    private val notification: NotificationService,
) : CommentLikeSaga {

    override suspend fun handleEvent(event: CommentLikeEvent) {
        when (event) {
            is CommentLikeCreated -> {
                // push
                notification.notify(event)
                // increment like
                commentLikeCountStore.increment(event.commentLike.postCommentId)
            }
            is CommentLikeDeleted -> {
                commentLikeCountStore.decrement(event.commentLike.postCommentId)
            }
        }
    }
}
