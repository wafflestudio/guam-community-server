package waffle.guam.user.service.block

import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.BlockEntity
import waffle.guam.user.infra.db.BlockRepository
import waffle.guam.user.service.Command
import waffle.guam.user.service.block.BlockCommandService.CreateBlock
import waffle.guam.user.service.block.BlockCommandService.DeleteBlock

interface BlockCommandService {

    fun createBlock(command: CreateBlock): Block

    fun deleteBlock(command: DeleteBlock)

    data class CreateBlock(
        val userId: Long,
        val blockUserId: Long,
    ) : Command

    data class DeleteBlock(
        val userId: Long,
        val blockUserId: Long,
    ) : Command
}

@Service
class BlockCommandServiceImpl(
    private val blockRepository: BlockRepository,
) : BlockCommandService {

    override fun createBlock(command: CreateBlock): Block {
        return blockRepository.save(
            BlockEntity(
                userId = command.userId,
                blockUserId = command.blockUserId
            )
        ).let(::Block)
    }

    override fun deleteBlock(command: DeleteBlock) {
        blockRepository.deleteByUserIdAndBlockUserId(
            userId = command.userId,
            blockUserId = command.blockUserId
        )
    }
}
