package waffle.guam.community.service.command

import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import waffle.guam.community.service.client.NotificationService

@Service
class EventHandler(
    private val notificationService: NotificationService,
) {
    @EventListener
    @Async
    fun handleNotification(event: NotifyingEventResult) = runBlocking {
        notificationService.notify(event.toRequest())
    }
}
