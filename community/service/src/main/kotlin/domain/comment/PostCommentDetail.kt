package waffle.guam.community.service.domain.comment

import com.fasterxml.jackson.annotation.JsonIgnore
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.favorite.api.model.CommentFavoriteInfo
import waffle.guam.user.domain.UserInfo
import java.time.Instant

data class PostCommentDetail(
    val id: Long,
    val postId: PostId,
    val user: UserInfo,
    val content: String,
    val imagePaths: List<String>,
    val mentionIds: List<Long>,
    val likeCount: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isMine: Boolean,
    val isLiked: Boolean,
) {
    @get:JsonIgnore
    val isAnonymous: Boolean
        get() = user.isAnonymous
}

fun PostCommentDetail(d: PostComment, favorite: CommentFavoriteInfo, callerId: UserId) =
    PostCommentDetail(
        postId = d.postId,
        id = d.id,
        user = d.user,
        content = d.content,
        imagePaths = d.imagePaths,
        mentionIds = d.mentionIds,
        likeCount = favorite.count,
        createdAt = d.createdAt,
        updatedAt = d.updatedAt,
        isMine = d.user.id == callerId,
        isLiked = favorite.like,
    )
