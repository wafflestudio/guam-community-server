package waffle.guam.community.service.domain.post

import waffle.guam.community.service.domain.tag.Tag
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostPreview(
    val id: Long,
    val boardId: Long,
    val user: User,
    val title: String,
    val content: String,
    val status: String,
    val isImageIncluded: Boolean,
//    val viewCount: Int,
//    val scrapCount: Int,
//    val commentCount: Int,
    val tags: List<Tag>,
    val likeCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)
