package waffle.guam.favorite.api.router

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import waffle.guam.favorite.service.command.ScrapCreateHandler
import waffle.guam.favorite.service.command.ScrapDeleteHandler
import waffle.guam.favorite.service.model.Scrap
import waffle.guam.favorite.service.query.ScrapUserStore

@Service
class ScrapApiRouter(
    private val scrapCreateHandler: ScrapCreateHandler,
    private val scrapDeleteHandler: ScrapDeleteHandler,
    private val scrapUserStore: ScrapUserStore,
) {

    suspend fun create(request: ServerRequest): ServerResponse {
        val userId = request.getHeader("X-GATEWAY-USER-ID").toLong()
        val postId = request.pathVariable("postId").toLong()

        scrapCreateHandler.handle(Scrap(postId = postId, userId = userId))

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(Unit))
    }

    suspend fun delete(request: ServerRequest): ServerResponse {
        val userId = request.getHeader("X-GATEWAY-USER-ID").toLong()
        val postId = request.pathVariable("postId").toLong()

        scrapDeleteHandler.handle(Scrap(postId = postId, userId = userId))

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(Unit))
    }

    suspend fun getUsers(request: ServerRequest): ServerResponse {
        val userId = request.getParam("userId").toLong()
        val page = request.getParam("page").toInt()

        val response = scrapUserStore.getScrappedPostIds(userId, page)

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(response))
    }
}
