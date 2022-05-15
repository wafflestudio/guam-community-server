package waffle.guam.community.service.command.notification

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.push.PushEventRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.PushEventResult

@Service
class CreatePushEventHandler(
    private val pushEventRepository: PushEventRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    @EventListener
    fun pushEventCreated(event: PushEventResult) {
        if (event.needNotNotify) return
        val userWhoTriggeredEvent = userRepository.getById(event.producedUserId)
        pushEventRepository.saveAll(event.toPushEventEntities(userWhoTriggeredEvent))
    }
}
