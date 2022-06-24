package waffle.guam.community.service.client

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.community.service.PostId

interface FavoriteService {
    fun getRankedPosts(userId: Long, rankFrom: Int, rankTo: Int): List<PostId>
    fun getPostFavorite(userId: Long, postId: Long): PostFavorite
    fun getPostFavorite(userId: Long, postIds: List<Long>): Map<Long, PostFavorite>
    fun getCommentFavorite(userId: Long, commentId: Long): CommentFavorite
    fun getCommentFavorite(userId: Long, commentIds: List<Long>): Map<Long, CommentFavorite>
    fun getUserScrappedPosts(userId: Long, page: Int): List<PostId>
}

@Service
class FavoriteServiceImpl(
    webClientBuilder: WebClient.Builder,
) : ClientService(webClientBuilder, BaseURL.FAVORITE), FavoriteService {

    override fun getRankedPosts(userId: Long, rankFrom: Int, rankTo: Int): List<PostId> = runBlocking {
        webClient.get()
            .uri("/api/v1/views/rank?from=$rankFrom&to=$rankTo&userId=$userId")
            .accept().retrieve()
            .awaitBody()
    }

    override fun getPostFavorite(userId: Long, postId: Long): PostFavorite = runBlocking {
        val response = webClient.get()
            .uri("/api/v1/views?postIds={postId}&userId={userId}", postId, userId)
            .accept()
            .retrieve()
            .awaitBody<PostFavoriteResponse>()

        return@runBlocking response.data.first()
    }

    override fun getPostFavorite(userId: Long, postIds: List<Long>): Map<Long, PostFavorite> = runBlocking {
        val response = webClient.get()
            .uri("/api/v1/views?postIds={postId}&userId={userId}", postIds.joinToString(","), userId)
            .accept()
            .retrieve()
            .awaitBody<PostFavoriteResponse>()

        return@runBlocking response.data.associateBy { it.postId }
    }

    override fun getCommentFavorite(userId: Long, commentId: Long): CommentFavorite = runBlocking {
        val response = webClient.get()
            .uri("/api/v1/likes/comments/count?postCommentIds={commentId}&userId={userId}", commentId, userId)
            .accept()
            .retrieve()
            .awaitBody<CommentFavoriteResponse>()

        return@runBlocking response.data.first()
    }

    override fun getCommentFavorite(userId: Long, commentIds: List<Long>): Map<Long, CommentFavorite> = runBlocking {
        val commentIdStr = commentIds.joinToString(",")

        val response = webClient.get()
            .uri("/api/v1/likes/comments/count?postCommentIds={commentId}&userId={userId}", commentIdStr, userId)
            .accept()
            .retrieve()
            .awaitBody<CommentFavoriteResponse>()

        return@runBlocking response.data.associateBy { it.postCommentId }
    }

    override fun getUserScrappedPosts(userId: Long, page: Int): List<PostId> = runBlocking {
        webClient.get()
            .uri("/api/v1/views/user?userId=$userId&page=$page")
            .accept().retrieve()
            .awaitBody<List<Long>>()
            .sortedDescending()
    }

    private data class PostFavoriteResponse(
        val data: List<PostFavorite>,
    )

    private data class CommentFavoriteResponse(
        val data: List<CommentFavorite>,
    )
}

data class PostFavorite(
    val postId: Long,
    val likeCnt: Int,
    val scrapCnt: Int,
    val like: Boolean,
    val scrap: Boolean,
)

data class CommentFavorite(
    val postCommentId: Long,
    val count: Int,
    val like: Boolean,
)
