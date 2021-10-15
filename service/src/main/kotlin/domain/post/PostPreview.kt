package waffle.guam.community.service.domain.post

import waffle.guam.community.service.domain.tag.PostTag
import waffle.guam.community.service.domain.user.User
import java.time.Instant

data class PostPreview(
    val id: Long,
    val boardId: Long,
    val user: User,
    val title: String,
    val content: String,
    val isImageIncluded: Boolean,
    val tags: List<PostTag>,
    val likeCount: Int,
    val commentCount: Int,
//    val viewCount: Int,
//    val scrapCount: Int,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
