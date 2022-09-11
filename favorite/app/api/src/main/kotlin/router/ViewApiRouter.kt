package waffle.guam.favorite.api.router

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import waffle.guam.favorite.data.redis.repository.PostLikeCountRepository
import waffle.guam.favorite.data.redis.repository.PostScrapCountRepository
import waffle.guam.favorite.service.query.LikeUserStore
import waffle.guam.favorite.service.query.ScrapUserStore

@RequestMapping("/api/v1/views")
@RestController
class ViewApiRouter(
    private val likeCountRepository: PostLikeCountRepository,
    private val likeUserStore: LikeUserStore,
    private val scrapCountRepository: PostScrapCountRepository,
    private val scrapUserStore: ScrapUserStore,
) {

    suspend fun get(request: ServerRequest): ServerResponse {
        val postIds = request.getParam("postIds")
            .takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { it.toLong() }
            ?: emptyList()
        val userId = request.getParam("userId").toLong()

        val response = getView(postIds, userId)

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(response))
    }

    suspend fun getRank(request: ServerRequest): ServerResponse {
        val boardId = request.queryParamOrNull("boardId")?.toLong()
        val from = request.getParam("from").toLong()
        val to = request.getParam("to").toLong()
        val userId = request.getParam("userId").toLong()

        val rank = likeCountRepository.getRank(boardId = boardId, from = from, to = to)
        val response = getView(postIds = rank, userId = userId)

        return ServerResponse.ok().bodyValueAndAwait(SuccessResponse(response))
    }

    private suspend fun getView(postIds: List<Long>, userId: Long) = coroutineScope {
        val likeCntMap = async { likeCountRepository.gets(postIds) }
        val likeMap = async { likeUserStore.getLiked(postIds, userId) }
        val scrapCntMap = async { scrapCountRepository.gets(postIds) }
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

    data class LikeScrapResponse(
        val postId: Long,
        val likeCnt: Long,
        val scrapCnt: Long,
        val like: Boolean,
        val scrap: Boolean,
    )
}
