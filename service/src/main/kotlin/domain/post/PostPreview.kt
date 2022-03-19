package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.board.BoardName
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.comment.PostComment
import waffle.guam.community.service.domain.like.PostLike
import waffle.guam.community.service.domain.scrap.PostScrap
import waffle.guam.community.service.domain.tag.PostTag
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
    val categories: List<PostTag>,
    val likeCount: Int,
    val commentCount: Int,
    val scrapCount: Int,
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
            tags: List<PostTag>?,
            likes: List<PostLike>?,
            scraps: List<PostScrap>?,
            comments: List<PostComment>?,
        ): PostPreview {
            return PostPreview(
                id = post.id,
                boardId = post.boardId,
                user = if (post.isAnonymous) AnonymousUser() else user,
                title = post.title,
                content = post.content,
                imagePaths = post.imagePaths,
                status = post.status,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
                categories = tags ?: listOf(),
                likeCount = likes?.size ?: 0,
                commentCount = comments?.size ?: 0,
                scrapCount = scraps?.size ?: 0,
                isLiked = likes?.any { like -> like.userId == user.id } ?: false,
                isScrapped = scraps?.any { scrap -> scrap.userId == user.id } ?: false,
            )
        }
    }
}
