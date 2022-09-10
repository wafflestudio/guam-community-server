package waffle.guam.favorite.client

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import waffle.guam.favorite.client.model.CommentInfo
import waffle.guam.favorite.client.model.PostInfo

interface GuamFavoriteClient {

    fun getPostRanking(userId: Long, boardId: Long?, rankFrom: Int, rankTo: Int): List<Long>
    fun getPostInfo(userId: Long, postId: Long): PostInfo
    fun getPostInfo(userId: Long, postIds: List<Long>): Map<Long, PostInfo>
    fun getCommentInfo(userId: Long, commentId: Long): CommentInfo
    fun getCommentInfo(userId: Long, commentIds: List<Long>): Map<Long, CommentInfo>
    fun getScrappedPosts(userId: Long, page: Int): List<Long>

    interface Async {
        fun getPostRanking(userId: Long, boardId: Long?, rankFrom: Int, rankTo: Int): Flux<Long>
        fun getPostInfo(userId: Long, postId: Long): Mono<PostInfo>
        fun getPostInfo(userId: Long, postIds: List<Long>): Mono<Map<Long, PostInfo>>
        fun getCommentInfo(userId: Long, commentId: Long): Mono<CommentInfo>
        fun getCommentInfo(userId: Long, commentIds: List<Long>): Mono<Map<Long, CommentInfo>>
        fun getScrappedPosts(userId: Long, page: Int): Flux<Long>
    }
}
