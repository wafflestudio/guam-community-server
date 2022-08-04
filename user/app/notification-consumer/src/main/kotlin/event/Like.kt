package waffle.guam.user.notification.event

import waffle.guam.user.infra.db.NotificationKind
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification.Info

internal data class Post(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val status: String,
    val isAnonymous: Boolean,
)

internal data class Like(
    val postId: Long,
    val userId: Long,
)

internal data class Comment(
    val id: Long,
    val postId: Long,
    val userId: Long,
    val content: String,
    val status: String,
    val isAnonymous: Boolean,
)

internal data class CommentLike(
    val postCommentId: Long,
    val userId: Long,
)

internal data class PostLikeCreated(
    val like: Like,
    val post: Post,
) : NotifyingEvent {
    override fun toRequest() = CreateNotification(
        producerId = like.userId,
        infos = listOf(
            Info(
                consumerId = post.userId,
                kind = NotificationKind.POST_LIKE.name,
                body = post.title,
                linkUrl = "/api/v1/posts/${post.id}",
                isAnonymousEvent = post.isAnonymous,
            )
        )
    )
}

internal data class CommentLikeCreated(
    val commentLike: CommentLike,
    val comment: Comment,
) : NotifyingEvent {
    override fun toRequest() = CreateNotification(
        producerId = commentLike.userId,
        infos = listOf(
            Info(
                consumerId = comment.userId,
                kind = NotificationKind.POST_COMMENT_LIKE.name,
                body = comment.content,
                linkUrl = "/api/v1/posts/${comment.postId}",
                isAnonymousEvent = comment.isAnonymous
            )
        )
    )
}
