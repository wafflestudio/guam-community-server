package waffle.guam.favorite.client.impl

import waffle.guam.favorite.client.GuamFavoriteClient
import waffle.guam.favorite.client.model.CommentInfo
import waffle.guam.favorite.client.model.PostInfo

internal class GuamFavoriteClientImpl(baseUrl: String) : GuamFavoriteClient {
    private val client = GuamFavoriteAsyncClientImpl(baseUrl)

    override fun getPostRanking(userId: Long, boardId: Long?, rankFrom: Int, rankTo: Int): List<Long> =
        client.getPostRanking(userId, boardId, rankFrom, rankTo).collectList().block()!!

    override fun getPostInfo(userId: Long, postId: Long): PostInfo =
        client.getPostInfo(userId, postId).block()!!

    override fun getPostInfo(userId: Long, postIds: List<Long>): Map<Long, PostInfo> =
        client.getPostInfo(userId, postIds).block()!!

    override fun getCommentInfo(userId: Long, commentId: Long): CommentInfo =
        client.getCommentInfo(userId, commentId).block()!!

    override fun getCommentInfo(userId: Long, commentIds: List<Long>): Map<Long, CommentInfo> =
        client.getCommentInfo(userId, commentIds).block()!!

    override fun getScrappedPosts(userId: Long, page: Int): List<Long> =
        client.getScrappedPosts(userId, page).collectList().block()!!
}
