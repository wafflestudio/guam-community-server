package waffle.guam.favorite.service.infra

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.favorite.service.ServiceProperties
import waffle.guam.favorite.service.command.CommentLikeCreated
import waffle.guam.favorite.service.command.LikeCreated
import waffle.guam.favorite.service.infra.NotificationServiceImpl.CreateNotificationRequest.Info
import waffle.guam.favorite.service.model.CommentLike
import waffle.guam.favorite.service.model.Like

interface NotificationService {
    suspend fun notify(event: LikeCreated)
    suspend fun notify(event: CommentLikeCreated)
}

@Service
class NotificationServiceImpl(
    private val community: CommunityService,
    properties: ServiceProperties,
    webClientBuilder: WebClient.Builder,
) : NotificationService {

    private val notification = webClientBuilder
        .baseUrl(properties.notification.url)
        .build()

    override suspend fun notify(event: LikeCreated) {
        val post = community.getPost(event.like.postId) ?: throw RuntimeException()

        notification.post()
            .uri("/api/v1/push/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(CreateNotificationRequest(like = event.like, post = post))
            .retrieve()
            .awaitBody<Unit>()
    }

    override suspend fun notify(event: CommentLikeCreated) {
        val comment = community.getComment(event.commentLike.postCommentId) ?: throw RuntimeException()

        notification.post()
            .uri("/api/v1/push/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(CreateNotificationRequest(like = event.commentLike, comment = comment))
            .retrieve()
            .awaitBody<Unit>()
    }

    data class CreateNotificationRequest(
        val producerId: Long,
        val infos: List<Info>,
    ) {
        data class Info(
            val consumerId: Long,
            val kind: String,
            val body: String,
            val linkUrl: String,
            val isAnonymousEvent: Boolean,
        )
    }

    private fun CreateNotificationRequest(like: Like, post: Post): CreateNotificationRequest {
        require(post.status == "VALID")

        return CreateNotificationRequest(
            producerId = like.userId,
            infos = listOf(
                Info(
                    consumerId = post.userId,
                    kind = "POST_LIKE",
                    body = post.title,
                    linkUrl = "/api/v1/posts/${post.id}",
                    isAnonymousEvent = post.isAnonymous
                )
            )
        )
    }

    private fun CreateNotificationRequest(like: CommentLike, comment: Comment): CreateNotificationRequest {
        require(comment.status == "VALID")

        return CreateNotificationRequest(
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
    }
}
