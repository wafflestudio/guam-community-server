package waffle.guam.community.service.domain.post

import waffle.guam.community.data.jdbc.board.BoardName
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.comment.PostComment
import waffle.guam.community.service.domain.tag.PostTag
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
}
