package waffle.guam.favorite.data.r2dbc

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ScrapRepository : CoroutineCrudRepository<ScrapEntity, Long> {
    suspend fun existsByPostIdAndUserId(postId: Long, userId: Long): Boolean
    suspend fun deleteByPostIdAndUserId(postId: Long, userId: Long)
    fun findAllByPostIdInAndUserId(postIds: List<Long>, userId: Long): Flow<ScrapEntity>
    fun findByUserId(userId: Long, pageable: Pageable): Flow<ScrapEntity>
}
