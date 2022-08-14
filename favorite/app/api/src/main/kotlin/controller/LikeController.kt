package waffle.guam.favorite.api.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.favorite.api.SuccessResponse
import waffle.guam.favorite.service.command.LikeCreateHandler
import waffle.guam.favorite.service.command.LikeDeleteHandler
import waffle.guam.favorite.service.model.Like

@RequestMapping("/api/v1/likes/posts")
@RestController
class LikeController(
    private val LikeCreateHandler: LikeCreateHandler,
    private val LikeDeleteHandler: LikeDeleteHandler,
) {

    @PostMapping("/{postId}")
    suspend fun create(
        @PathVariable postId: Long,
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): SuccessResponse<Unit> {
        LikeCreateHandler.handle(Like(postId = postId, userId = userId))

        return SuccessResponse(Unit)
    }

    @DeleteMapping("/{postId}")
    suspend fun delete(
        @PathVariable postId: Long,
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): SuccessResponse<Unit> {
        LikeDeleteHandler.handle(Like(postId = postId, userId = userId))

        return SuccessResponse(Unit)
    }
}
