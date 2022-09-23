package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.board.BoardName
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.client.PostFavorite
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.comment.PostCommentDetail
import waffle.guam.user.domain.UserInfo
import java.time.Instant

data class PostDetail(
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
    val comments: List<PostCommentDetail>,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isMine: Boolean,
    val isLiked: Boolean,
    val isScrapped: Boolean,
) {
    val boardType = BoardName.of(boardId)
}

fun PostDetail(
    post: Post,
    user: UserInfo,
    category: PostCategory?,
    comments: List<PostCommentDetail>,
    callerId: Long,
    favorite: PostFavorite,
): PostDetail {
    require(post.isAnonymous == user.isAnonymous)
    if (comments.isNotEmpty()) {
        require(post.isAnonymous == comments.all { it.isAnonymous })
    }

    return PostDetail(
        id = post.id,
        boardId = post.boardId,
        user = user,
        title = post.title,
        content = post.content,
        imagePaths = post.imagePaths,
        category = category,
        commentCount = comments.size,
        comments = comments,
        status = post.status,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
        isMine = post.userId == callerId,
        isLiked = favorite.like,
        likeCount = favorite.likeCnt,
        isScrapped = favorite.scrap,
        scrapCount = favorite.scrapCnt
    )
}
