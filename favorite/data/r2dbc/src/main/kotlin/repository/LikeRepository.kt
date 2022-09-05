package waffle.guam.favorite.data.r2dbc.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import waffle.guam.favorite.data.r2dbc.entity.LikeEntity

interface LikeRepository : CoroutineCrudRepository<LikeEntity, Long> {
    suspend fun existsByPostIdAndUserId(postId: Long, userId: Long): Boolean
    suspend fun deleteByPostIdAndUserId(postId: Long, userId: Long): Int
    fun findAllByPostIdInAndUserId(postIds: List<Long>, userId: Long): Flow<LikeEntity>
}
