package waffle.guam.favorite.data.r2dbc

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CommentLikeRepository : CoroutineCrudRepository<CommentLikeEntity, Long> {
    suspend fun existsByPostCommentIdAndUserId(postCommentId: Long, userId: Long): Boolean
    suspend fun deleteByPostCommentIdAndUserId(postCommentId: Long, userId: Long): Int
    fun findAllByPostCommentIdInAndUserId(postCommentIds: List<Long>, userId: Long): Flow<CommentLikeEntity>
}
