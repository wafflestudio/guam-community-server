package waffle.guam.user.infra.db

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): Page<NotificationEntity>
}
