package waffle.guam.user.infra.db

import org.springframework.data.jpa.repository.JpaRepository

interface BlockRepository : JpaRepository<BlockEntity, Long> {
    fun findAllByUserId(userId: Long): List<BlockEntity>
    fun deleteByUserIdAndBlockUserId(userId: Long, blockUserId: Long)
}
