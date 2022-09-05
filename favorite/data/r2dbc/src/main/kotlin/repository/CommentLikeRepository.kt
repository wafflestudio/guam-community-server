package waffle.guam.favorite.data.r2dbc.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import waffle.guam.favorite.data.r2dbc.entity.CommentLikeEntity

interface CommentLikeRepository : CoroutineCrudRepository<CommentLikeEntity, Long> {
    suspend fun existsByPostCommentIdAndUserId(postCommentId: Long, userId: Long): Boolean
    suspend fun deleteByPostCommentIdAndUserId(postCommentId: Long, userId: Long): Int
    fun findAllByPostCommentIdInAndUserId(postCommentIds: List<Long>, userId: Long): Flow<CommentLikeEntity>
}
