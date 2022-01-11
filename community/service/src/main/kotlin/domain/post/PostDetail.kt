package waffle.guam.community.service.domain.post

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
    val tags: List<PostTag>,
    val likeCount: Int,
    val commentCount: Int,
    val comments: List<PostComment>,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
