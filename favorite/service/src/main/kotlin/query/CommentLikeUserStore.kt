package waffle.guam.favorite.service.query

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.r2dbc.repository.CommentLikeRepository

// FIXME
interface CommentLikeUserStore {
    suspend fun getLiked(postCommentId: Long, userId: Long): Boolean
    suspend fun getLiked(postCommentIds: List<Long>, userId: Long): Map<Long, Boolean>
}

@Service
class CommentLikeUserStoreImpl(
    private val commentLikeRepository: CommentLikeRepository,
) : CommentLikeUserStore {
    override suspend fun getLiked(postCommentId: Long, userId: Long): Boolean {
        return commentLikeRepository.existsByPostCommentIdAndUserId(
            postCommentId = postCommentId,
            userId = userId
        )
    }

    override suspend fun getLiked(postCommentIds: List<Long>, userId: Long): Map<Long, Boolean> {
        return commentLikeRepository.findAllByPostCommentIdInAndUserId(
            postCommentIds = postCommentIds,
            userId = userId
        )
            .toList()
            .map { it.postCommentId }
            .toSet()
            .let { likedCommentIds -> postCommentIds.associateWith { likedCommentIds.contains(it) } }
    }
}
