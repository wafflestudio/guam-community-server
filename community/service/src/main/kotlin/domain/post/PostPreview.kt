package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostFavorite
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostPreview(
    val id: PostId,
    val boardId: BoardId,
    val user: User,
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
)

fun PostPreview(
    post: PostEntity,
    user: User,
    favorite: PostFavorite,
    callerId: Long,
): PostPreview {
    return PostPreview(
        id = post.id,
        boardId = post.boardId,
        user = if (post.isAnonymous) AnonymousUser() else user,
        title = post.title,
        content = post.content,
        imagePaths = post.images,
        status = post.status.name,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
        category = post.categories.singleOrNull()?.let(::PostCategory),
        likeCount = favorite.likeCnt,
        commentCount = post.comments.size,
        scrapCount = favorite.scrapCnt,
        isMine = post.userId == callerId,
        isLiked = favorite.like,
        isScrapped = favorite.scrap
    )
}

fun PostPreview(
    userId: Long,
    post: PostEntity,
    users: Map<UserId, User>,
    favorites: Map<PostId, PostFavorite>,
) = PostPreview(
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
    user = if (post.isAnonymous) AnonymousUser() else users[post.userId]!!,
    likeCount = favorites[post.id]!!.likeCnt,
    scrapCount = favorites[post.id]!!.scrapCnt,
    isLiked = favorites[post.id]!!.like,
    isScrapped = favorites[post.id]!!.scrap,
)
