package waffle.guam.user.service.block

import waffle.guam.user.infra.db.BlockEntity

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
    val blockUserIds: List<Long>,
)
