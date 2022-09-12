package waffle.guam.favorite.service.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.service.domain.Block
import waffle.guam.letter.data.r2dbc.data.BlockEntity
import waffle.guam.letter.data.r2dbc.repository.BlockRepository

interface BlockCommandService {
    suspend fun createBlock(command: CreateBlock): Block
}

@Service
class BlockCommandServiceImpl(
    private val blockRepository: BlockRepository,
) : BlockCommandService {
    @Transactional
    override suspend fun createBlock(command: CreateBlock): Block {
        return blockRepository.save(
            BlockEntity(userId = command.userId, blockUserId = command.targetId)
        ).let {
            Block(userId = it.userId, targetId = it.blockUserId)
        }
    }
}

data class CreateBlock(
    val userId: Long,
    val targetId: Long,
)
