package waffle.guam.favorite.api.router

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import waffle.guam.favorite.service.command.LikeCreateHandler
import waffle.guam.favorite.service.command.LikeDeleteHandler
import waffle.guam.favorite.service.model.Like

@RequestMapping("/api/v1/likes/posts")
@RestController
class LikeApiRouter(
    private val likeCreateHandler: LikeCreateHandler,
    private val likeDeleteHandler: LikeDeleteHandler,
) {

    suspend fun create(request: ServerRequest): ServerResponse {
        val userId = request.getHeader("X-GATEWAY-USER-ID").toLong()
        val postId = request.pathVariable("postId").toLong()

        likeCreateHandler.handle(Like(postId = postId, userId = userId))

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(Unit))
    }

    suspend fun delete(request: ServerRequest): ServerResponse {
        val userId = request.getHeader("X-GATEWAY-USER-ID").toLong()
        val postId = request.pathVariable("postId").toLong()

        likeDeleteHandler.handle(Like(postId = postId, userId = userId))

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(Unit))
    }
}
