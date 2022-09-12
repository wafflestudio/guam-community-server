package waffle.guam.letter.data.r2dbc.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import waffle.guam.letter.data.r2dbc.data.BlockEntity

interface BlockRepository : CoroutineCrudRepository<BlockEntity, Long> {
    fun findAllBlockUserIdsByUserId(userId: Long): Flow<Long>
}
