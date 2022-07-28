package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.CommentLikeCreated
import waffle.guam.favorite.service.command.CommentLikeDeleted
import waffle.guam.favorite.service.command.CommentLikeEvent
import waffle.guam.favorite.service.infra.Comment
import waffle.guam.favorite.service.infra.CommunityService
import waffle.guam.favorite.service.infra.NotificationService
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest
import waffle.guam.favorite.service.infra.NotificationService.CreateNotificationRequest.Info
import waffle.guam.favorite.service.model.CommentLike
import waffle.guam.favorite.service.query.CommentLikeCountStore

interface CommentLikeSaga {
    suspend fun handleEvent(event: CommentLikeEvent)
}

@Service
class CommentLikeSagaImpl(
    private val commentLikeCountStore: CommentLikeCountStore.Mutable,
    private val notification: NotificationService,
    private val community: CommunityService,
) : CommentLikeSaga {

    override suspend fun handleEvent(event: CommentLikeEvent) {
        val comment =
            community.getComment(event.commentLike.postCommentId) ?: throw RuntimeException("COMMENT NOT FOUND")

        when (event) {
            is CommentLikeCreated -> {
                // push
                notification.notify(CreateNotificationRequest(comment = comment, like = event.commentLike))
                // increment like
                commentLikeCountStore.increment(event.commentLike.postCommentId)
            }
            is CommentLikeDeleted -> {
                commentLikeCountStore.decrement(event.commentLike.postCommentId)
            }
        }
    }
}

private fun CreateNotificationRequest(like: CommentLike, comment: Comment): CreateNotificationRequest =
    CreateNotificationRequest(
        producerId = like.userId,
        infos = listOf(
            Info(
                consumerId = comment.userId,
                kind = "POST_COMMENT_LIKE",
                body = comment.content,
                linkUrl = "/api/v1/posts/${comment.postId}",
                isAnonymousEvent = comment.isAnonymous
            )
        )
    )
