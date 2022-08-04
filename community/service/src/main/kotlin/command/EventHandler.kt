package waffle.guam.community.service.command

import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import waffle.guam.community.service.CommunityKafkaProducer

@Service
class EventHandler(
    private val communityKafkaProducer: CommunityKafkaProducer,
) {
    @EventListener
    @Async
    fun handleNotification(event: NotifyingEventResult) = runBlocking {
        communityKafkaProducer.send(event)
    }
}
