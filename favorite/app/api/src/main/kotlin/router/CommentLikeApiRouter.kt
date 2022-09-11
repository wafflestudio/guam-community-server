package waffle.guam.favorite.api.router

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import waffle.guam.favorite.data.redis.repository.CommentLikeCountRepository
import waffle.guam.favorite.service.command.CommentLikeCreateHandler
import waffle.guam.favorite.service.command.CommentLikeDeleteHandler
import waffle.guam.favorite.service.model.CommentLike
import waffle.guam.favorite.service.query.CommentLikeUserStore

@Service
class CommentLikeApiRouter(
    private val commentLikeCreateHandler: CommentLikeCreateHandler,
    private val commentLikeDeleteHandler: CommentLikeDeleteHandler,
    private val commentLikeCountRepository: CommentLikeCountRepository,
    private val commentLikeUserStore: CommentLikeUserStore,
) {

    suspend fun create(request: ServerRequest): ServerResponse {
        val userId = request.getHeader("X-GATEWAY-USER-ID").toLong()
        val postCommentId = request.pathVariable("postCommentId").toLong()

        commentLikeCreateHandler.handle(
            CommentLike(
                postCommentId = postCommentId,
                userId = userId
            )
        )

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(Unit))
    }

    suspend fun delete(request: ServerRequest): ServerResponse {
        val userId = request.getHeader("X-GATEWAY-USER-ID").toLong()
        val postCommentId = request.pathVariable("postCommentId").toLong()

        commentLikeDeleteHandler.handle(
            CommentLike(
                postCommentId = postCommentId,
                userId = userId
            )
        )

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(Unit))
    }

    suspend fun gets(request: ServerRequest): ServerResponse {
        val postCommentIds = request.getParam("postCommentIds")
            .takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { it.toLong() }
            ?: emptyList()
        val userId = request.getParam("userId").toLong()

        val response = coroutineScope {
            val countMap = async { commentLikeCountRepository.gets(postCommentIds) }
            val likedMap = async { commentLikeUserStore.getLiked(postCommentIds, userId) }

            postCommentIds.map {
                PostCommentLikeResponse(
                    postCommentId = it,
                    count = countMap.await()[it]!!,
                    like = likedMap.await()[it]!!
                )
            }
        }

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(response))
    }

    data class PostCommentLikeResponse(
        val postCommentId: Long,
        val count: Long,
        val like: Boolean,
    )
}
