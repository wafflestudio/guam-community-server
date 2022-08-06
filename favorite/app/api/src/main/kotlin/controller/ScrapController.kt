package waffle.guam.favorite.api.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.favorite.api.SuccessResponse
import waffle.guam.favorite.service.command.ScrapCreateHandler
import waffle.guam.favorite.service.command.ScrapDeleteHandler
import waffle.guam.favorite.service.model.Scrap
import waffle.guam.favorite.service.query.ScrapUserStore

@RequestMapping("/api/v1/scraps")
@RestController
class ScrapController(
    private val scrapCreateHandler: ScrapCreateHandler,
    private val scrapDeleteHandler: ScrapDeleteHandler,
    private val scrapUserStore: ScrapUserStore,
) {

    @PostMapping("/posts/{postId}")
    suspend fun create(
        @PathVariable postId: Long,
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): SuccessResponse<Unit> {
        scrapCreateHandler.handle(
            Scrap(postId = postId, userId = userId)
        )

        return SuccessResponse(Unit)
    }

    @DeleteMapping("/posts/{postId}")
    suspend fun delete(
        @PathVariable postId: Long,
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): SuccessResponse<Unit> {
        scrapDeleteHandler.handle(
            Scrap(postId = postId, userId = userId)
        )

        return SuccessResponse(Unit)
    }

    @GetMapping("/user")
    suspend fun getUserScrappedPostIds(
        @RequestParam userId: Long,
        @RequestParam page: Int,
    ): SuccessResponse<List<Long>> {
        val response = scrapUserStore.getScrappedPostIds(userId, page)
        return SuccessResponse(response)
    }
}
