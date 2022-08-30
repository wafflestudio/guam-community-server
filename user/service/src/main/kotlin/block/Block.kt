package waffle.guam.user.service.block

import waffle.guam.user.infra.db.BlockEntity
import waffle.guam.user.service.user.User

data class Block(
    val userId: Long,
    val blockUserId: Long,
)

fun Block(entity: BlockEntity) = Block(
    userId = entity.userId,
    blockUserId = entity.blockUserId
)

data class BlockList(
    val userId: Long,
    val blockUsers: List<User>,
)
