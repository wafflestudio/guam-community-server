package waffle.guam.favorite.client.impl

import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import waffle.guam.favorite.client.GuamFavoriteClient
import waffle.guam.favorite.client.model.CommentInfo
import waffle.guam.favorite.client.model.PostInfo
import java.util.Optional

internal class GuamFavoriteAsyncClientImpl(url: String) : GuamFavoriteClient.Async {
    private val client = WebClient.builder()
        .baseUrl(url)
        .build()

    override fun getPostRanking(userId: Long, boardId: Long?, rankFrom: Int, rankTo: Int): Flux<Long> =
        client.get()
            .uri {
                it.path("/api/v1/views/rank")
                    .queryParamIfPresent("boardId", Optional.ofNullable(boardId))
                    .queryParam("userId", userId)
                    .queryParam("from", rankFrom)
                    .queryParam("to", rankTo)
                    .build()
            }
            .accept()
            .retrieve()
            .bodyToMono(PostInfoResponse::class.java)
            .map { it.data.map { it.postId } }
            .flatMapMany { Flux.fromIterable(it) }

    override fun getPostInfo(userId: Long, postId: Long): Mono<PostInfo> =
        client.get()
            .uri("/api/v1/views?postIds={postId}&userId={userId}", postId, userId)
            .accept()
            .retrieve()
            .bodyToMono(PostInfoResponse::class.java)
            .map { it.data.first() }

    override fun getPostInfo(userId: Long, postIds: List<Long>): Mono<Map<Long, PostInfo>> =
        client.get()
            .uri("/api/v1/views?postIds={postId}&userId={userId}", postIds.joinToString(","), userId)
            .accept()
            .retrieve()
            .bodyToMono(PostInfoResponse::class.java)
            .map { it.data.associateBy { it.postId } }

    override fun getCommentInfo(userId: Long, commentId: Long): Mono<CommentInfo> =
        client.get()
            .uri("/api/v1/likes/comments/count?postCommentIds={commentId}&userId={userId}", commentId, userId)
            .accept()
            .retrieve()
            .bodyToMono(CommentInfoResponse::class.java)
            .map { it.data.first() }

    override fun getCommentInfo(userId: Long, commentIds: List<Long>): Mono<Map<Long, CommentInfo>> =
        client.get()
            .uri(
                "/api/v1/likes/comments/count?postCommentIds={commentId}&userId={userId}",
                commentIds.joinToString(","),
                userId
            )
            .accept()
            .retrieve()
            .bodyToMono(CommentInfoResponse::class.java)
            .map { it.data.associateBy { it.postCommentId } }

    override fun getScrappedPosts(userId: Long, page: Int): Flux<Long> =
        client.get()
            .uri("/api/v1/scraps/user?userId=$userId&page=$page")
            .accept().retrieve()
            .bodyToMono(ScrappedIdsResponse::class.java)
            .map { it.data.sortedDescending() }
            .flatMapMany { Flux.fromIterable(it) }

    private data class PostInfoResponse(
        val data: List<PostInfo>,
    )

    private data class CommentInfoResponse(
        val data: List<CommentInfo>,
    )

    private data class ScrappedIdsResponse(
        val data: List<Long>,
    )
}
