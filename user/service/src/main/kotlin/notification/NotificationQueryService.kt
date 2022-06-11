package waffle.guam.user.service.notification

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.NotificationRepository

interface NotificationQueryService {
    fun getNotificationList(userId: Long, page: Int, size: Int): NotificationsPage
}

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
) : NotificationQueryService {

    override fun getNotificationList(userId: Long, page: Int, size: Int): NotificationsPage {
        val pagedEntities = notificationRepository.findAllByUserIdOrderByIdDesc(
            userId = userId,
            pageable = PageRequest.of(page, size)
        )

        return NotificationsPage(
            userId = userId,
            content = pagedEntities.content.map(::Notification),
            hasNext = pagedEntities.hasNext()
        )
    }
}
