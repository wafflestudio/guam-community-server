package waffle.guam.community.data.jdbc.push

import org.springframework.data.jpa.repository.JpaRepository

interface PushEventRepository : JpaRepository<PushEventEntity, Long> {
    fun findAllByUserId(userId: Long): List<PushEventEntity>
}
