package waffle.guam.favorite.service.query

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.letter.data.r2dbc.repository.BlockRepository

interface BlockQueryService {
    fun getBlockedPairs(userId: Long): Flow<Long>
}

@Service
class BlockQueryServiceImpl(
    private val blockRepository: BlockRepository,
) : BlockQueryService {
    @Transactional
    override fun getBlockedPairs(userId: Long): Flow<Long> {
        return blockRepository.findAllBlockUserIdsByUserId(userId)
    }
}
