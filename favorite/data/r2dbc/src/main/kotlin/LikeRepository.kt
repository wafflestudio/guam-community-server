package waffle.guam.favorite.data.r2dbc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Component

interface LikeRepository : CoroutineCrudRepository<LikeEntity, Long>, LikeCustomRepository {
    suspend fun existsByPostIdAndUserId(postId: Long, userId: Long): Boolean
    suspend fun deleteByPostIdAndUserId(postId: Long, userId: Long): Int
    fun findAllByPostIdInAndUserId(postIds: List<Long>, userId: Long): Flow<LikeEntity>
}

interface LikeCustomRepository {
    suspend fun countAll(pageable: Pageable): List<PostLikeCount>
}

@Component
class LikeCustomRepositoryImpl(
    private val dbClient: DatabaseClient
) : LikeCustomRepository {
    override suspend fun countAll(pageable: Pageable): List<PostLikeCount> {
        return dbClient
            .sql(
                """
                select post_id, count(id) as cnt
                from post_likes group by post_id
                limit :pageSize offset :offset
                """.trimIndent()
            )
            .bind("pageSize", pageable.pageSize)
            .bind("offset", pageable.offset)
            .map { row ->
                PostLikeCount(
                    postId = (row.get("post_id") as Number).toLong(),
                    count = (row.get("cnt") as Number).toLong(),
                )
            }
            .flow()
            .toList()
    }
}

data class PostLikeCount(
    val postId: Long,
    val count: Long
)
