package waffle.guam.community.service.domain.post

import com.fasterxml.jackson.annotation.JsonIgnore
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.client.PostFavorite
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.user.domain.UserInfo
import java.time.Instant

data class PostPreview(
    val id: PostId,
    val boardId: BoardId,
    val user: UserInfo,
    val title: String,
    val content: String,
    val imagePaths: List<String>,
    val category: PostCategory?,
    val likeCount: Int,
    val commentCount: Int,
    val scrapCount: Int,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isMine: Boolean,
    val isLiked: Boolean,
    val isScrapped: Boolean,
) {
    @get:JsonIgnore
    val isAnonymous: Boolean
        get() = boardId == 1L
}

fun PostPreview(
    userId: Long,
    post: PostEntity,
    user: UserInfo,
    favorite: PostFavorite,
): PostPreview {
    require(!post.isAnonymous)
    return PostPreview(
        id = post.id,
        boardId = post.boardId,
        title = post.title,
        content = post.content,
        imagePaths = post.images,
        commentCount = post.comments.size,
        status = post.status.name,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
        isMine = post.userId == userId,
        category = post.categories.firstOrNull()?.let(::PostCategory),
        user = user,
        likeCount = favorite.likeCnt,
        scrapCount = favorite.scrapCnt,
        isLiked = favorite.like,
        isScrapped = favorite.scrap,
    )
}

fun AnonymousPostPreview(
    userId: Long,
    post: PostEntity,
    favorite: PostFavorite,
): PostPreview {
    require(post.isAnonymous)
    return PostPreview(
        id = post.id,
        boardId = post.boardId,
        title = post.title,
        content = post.content,
        imagePaths = post.images,
        commentCount = post.comments.size,
        status = post.status.name,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
        isMine = post.userId == userId,
        category = post.categories.firstOrNull()?.let(::PostCategory),
        user = AnonymousUser(),
        likeCount = favorite.likeCnt,
        scrapCount = favorite.scrapCnt,
        isLiked = favorite.like,
        isScrapped = favorite.scrap,
    )
}
