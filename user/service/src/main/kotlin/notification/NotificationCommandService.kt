package waffle.guam.user.service.notification

import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.NotificationEntity
import waffle.guam.user.infra.db.NotificationKind
import waffle.guam.user.infra.db.NotificationRepository
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.service.CommandService
import waffle.guam.user.service.UnAuthorized
import waffle.guam.user.service.UserNotFound
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification
import waffle.guam.user.service.notification.NotificationCommandService.ReadNotification
import javax.transaction.Transactional

interface NotificationCommandService : CommandService {
    fun create(command: CreateNotification): List<Notification>
    fun read(command: ReadNotification): List<Notification>

    data class CreateNotification(
        val producerId: Long,
        val infos: List<Info>,
    ) {
        data class Info(
            val consumerId: Long,
            val kind: String,
            val body: String,
            val linkUrl: String,
            val isAnonymousEvent: Boolean,
        )
    }

    data class ReadNotification(
        val userId: Long,
        val notificationIds: List<Long>,
    )
}

@Service
class NotificationCommandServiceImpl(
    private val repository: NotificationRepository,
    private val userRepository: UserRepository,
) : NotificationCommandService {

    @Transactional
    override fun create(command: CreateNotification): List<Notification> {
        val producer = userRepository.findById(command.producerId).orElseThrow(::UserNotFound)

        val toCreate = command.infos
            .filterNot { command.producerId == it.consumerId } // 자기 자신에게는 보내지 않는다.
            .map {
                NotificationEntity(
                    userId = it.consumerId,
                    writer = producer,
                    kind = NotificationKind.valueOf(it.kind),
                    body = it.body.take(50), // 50자까지만 저장
                    linkUrl = it.linkUrl,
                    isAnonymousEvent = it.isAnonymousEvent,
                    isRead = false
                )
            }

        return repository.saveAll(toCreate).map(::Notification)
    }

    @Transactional
    override fun read(command: ReadNotification): List<Notification> {
        val (mine, others) = repository.findAllById(command.notificationIds)
            .partition { it.userId == command.userId }

        if (others.isNotEmpty()) {
            throw UnAuthorized()
        }

        mine.forEach { it.isRead = true }

        return mine.map(::Notification)
    }
}
