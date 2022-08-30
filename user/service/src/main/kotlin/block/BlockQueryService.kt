package waffle.guam.user.service.block

import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.BlockRepository
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.service.user.User

interface BlockQueryService {

    fun getBlockList(userId: Long): BlockList
}

@Service
class BlockQueryServiceImpl(
    private val blockRepository: BlockRepository,
    private val userRepository: UserRepository,
) : BlockQueryService {

    override fun getBlockList(userId: Long): BlockList {
        return blockRepository.findAllByUserId(userId)
            .map { it.blockUserId }
            .let { BlockList(userId = userId, blockUsers = userRepository.findAllById(it).map(::User)) }
    }
}
