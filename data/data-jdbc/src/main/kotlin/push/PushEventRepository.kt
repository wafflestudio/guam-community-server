package waffle.guam.community.data.jdbc.push

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PushEventRepository : JpaRepository<PushEventEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): Page<PushEventEntity>
}
