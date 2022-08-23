package waffle.guam.user.service.block

import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.BlockRepository

interface BlockQueryService {

    fun getBlockList(userId: Long): BlockList
}

@Service
class BlockQueryServiceImpl(
    private val blockRepository: BlockRepository,
) : BlockQueryService {

    override fun getBlockList(userId: Long): BlockList {
        return blockRepository.findAllByUserId(userId)
            .map { it.blockUserId }
            .let { BlockList(userId = userId, blockUserIds = it) }
    }
}
