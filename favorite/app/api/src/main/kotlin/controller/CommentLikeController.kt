package waffle.guam.favorite.api.controller

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.favorite.api.SuccessResponse
import waffle.guam.favorite.service.command.CommentLikeCreateHandler
import waffle.guam.favorite.service.command.CommentLikeDeleteHandler
import waffle.guam.favorite.service.model.CommentLike
import waffle.guam.favorite.service.query.CommentLikeCountStore
import waffle.guam.favorite.service.query.CommentLikeUserStore

@RequestMapping("/api/v1/likes/comments")
@RestController
class CommentLikeController(
    private val commentLikeCreateHandler: CommentLikeCreateHandler,
    private val commentLikeDeleteHandler: CommentLikeDeleteHandler,
    private val commentLikeCountStore: CommentLikeCountStore.Rank,
    private val commentLikeUserStore: CommentLikeUserStore,
) {

    @PostMapping("/{postCommentId}")
    suspend fun create(
        @PathVariable postCommentId: Long,
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): SuccessResponse<Unit> {
        commentLikeCreateHandler.handle(
            CommentLike(
                postCommentId = postCommentId,
                userId = userId
            )
        )

        return SuccessResponse(Unit)
    }

    @DeleteMapping("/{postCommentId}")
    suspend fun delete(
        @PathVariable postCommentId: Long,
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): SuccessResponse<Unit> {
        commentLikeDeleteHandler.handle(
            CommentLike(
                postCommentId = postCommentId,
                userId = userId
            )
        )

        return SuccessResponse(Unit)
    }

    @GetMapping("/count")
    suspend fun getCounts(
        @RequestParam postCommentIds: List<Long>,
        @RequestParam userId: Long,
    ): SuccessResponse<List<PostCommentLikeResponse>> {
        val response = coroutineScope {
            val countMap = async { commentLikeCountStore.getCount(postCommentIds) }
            val likedMap = async { commentLikeUserStore.getLiked(postCommentIds, userId) }

            postCommentIds.map {
                PostCommentLikeResponse(
                    postCommentId = it,
                    count = countMap.await()[it]!!,
                    like = likedMap.await()[it]!!
                )
            }
        }

        return SuccessResponse(response)
    }

    data class PostCommentLikeResponse(
        val postCommentId: Long,
        val count: Int,
        val like: Boolean,
    )
}
