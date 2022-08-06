package waffle.guam.favorite.api.controller

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.favorite.api.SuccessResponse
import waffle.guam.favorite.service.query.LikeCountStore
import waffle.guam.favorite.service.query.LikeUserStore
import waffle.guam.favorite.service.query.ScrapCountStore
import waffle.guam.favorite.service.query.ScrapUserStore
import javax.annotation.PostConstruct

@RequestMapping("/api/v1/views")
@RestController
class ViewsController(
    private val likeCountStore: LikeCountStore.Rank,
    private val likeUserStore: LikeUserStore,
    private val scrapCountStore: ScrapCountStore,
    private val scrapUserStore: ScrapUserStore,
) {

    @GetMapping
    suspend fun getPostLikeScrap(
        @RequestParam postIds: List<Long>,
        @RequestParam userId: Long,
    ): SuccessResponse<List<LikeScrapResponse>> {
        val response = coroutineScope {
            val likeCntMap = async { likeCountStore.getCount(postIds) }
            val likeMap = async { likeUserStore.getLiked(postIds, userId) }
            val scrapCntMap = async { scrapCountStore.getCount(postIds) }
            val scrapMap = async { scrapUserStore.getScraped(postIds, userId) }

            postIds.map {
                LikeScrapResponse(
                    postId = it,
                    likeCnt = likeCntMap.await()[it]!!,
                    scrapCnt = scrapCntMap.await()[it]!!,
                    like = likeMap.await()[it]!!,
                    scrap = scrapMap.await()[it]!!,
                )
            }
        }

        return SuccessResponse(response)
    }

    @GetMapping("/rank")
    suspend fun getRankedPostLikeScrap(
        @RequestParam(required = false) boardId: Long? = null,
        @RequestParam from: Int, // inclusive
        @RequestParam to: Int, // inclusive
        @RequestParam userId: Long,
    ): SuccessResponse<List<LikeScrapResponse>> {
        val rank = likeCountStore.getRank(boardId = boardId, from = from, to = to)

        return getPostLikeScrap(postIds = rank, userId = userId)
    }

    data class LikeScrapResponse(
        val postId: Long,
        val likeCnt: Int,
        val scrapCnt: Int,
        val like: Boolean,
        val scrap: Boolean,
    )
}
