package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.board.BoardName
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.comment.AnonymousComments
import waffle.guam.community.service.domain.comment.PostComment
import waffle.guam.community.service.domain.like.PostLike
import waffle.guam.community.service.domain.scrap.PostScrap
import waffle.guam.community.service.domain.tag.PostTag
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostDetail(
    val id: PostId,
    val boardId: BoardId,
    val user: User,
    val title: String,
    val content: String,
    val imagePaths: List<String>,
    val categories: List<PostTag>, // todo 엔티티도 네이밍 변경
    val likeCount: Int,
    val commentCount: Int,
    val scrapCount: Int,
    val comments: List<PostComment>,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isLiked: Boolean,
    val isScrapped: Boolean,
) {
    val boardType = BoardName.of(boardId)

    companion object {
        fun of(
            post: Post,
            user: User,
            tags: List<PostTag>,
            likes: List<PostLike>,
            scraps: List<PostScrap>,
            comments: List<PostComment>,
            callerId: Long,
        ): PostDetail {
            return PostDetail(
                id = post.id,
                boardId = post.boardId,
                user = if (post.isAnonymous) AnonymousUser() else user,
                title = post.title,
                content = post.content,
                imagePaths = post.imagePaths,
                categories = tags,
                likeCount = likes.size,
                commentCount = comments.size,
                comments = if (post.isAnonymous) AnonymousComments(comments, post.userId) else comments,
                status = post.status,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
                isLiked = likes.any { it.userId == callerId },
                isScrapped = scraps.any { it.userId == callerId },
                scrapCount = scraps.size,
            )
        }
    }
}
