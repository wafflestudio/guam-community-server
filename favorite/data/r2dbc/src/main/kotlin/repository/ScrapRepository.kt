package waffle.guam.favorite.data.r2dbc.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import waffle.guam.favorite.data.r2dbc.entity.ScrapEntity

interface ScrapRepository : CoroutineCrudRepository<ScrapEntity, Long> {
    suspend fun existsByPostIdAndUserId(postId: Long, userId: Long): Boolean
    suspend fun deleteByPostIdAndUserId(postId: Long, userId: Long): Int
    fun findAllByPostIdInAndUserId(postIds: List<Long>, userId: Long): Flow<ScrapEntity>
    fun findByUserId(userId: Long, pageable: Pageable): Flow<ScrapEntity>
}
