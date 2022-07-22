package waffle.guam.community.service.command

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.service.client.NotificationService

@Service
class EventHandler(
    private val notificationService: NotificationService,
) {
    @EventListener
    fun handle(event: EventResult) {

    }
}
