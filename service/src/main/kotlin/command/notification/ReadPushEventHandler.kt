package waffle.guam.community.service.command.notification

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.GuamNotFound
import waffle.guam.community.common.GuamUnAuthorized
import waffle.guam.community.data.jdbc.push.PushEventRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class ReadPushEventHandler(
    private val pushEventRepository: PushEventRepository,
) : CommandHandler<ReadPushEvent, PushEventRead> {

    @Transactional
    override fun handle(command: ReadPushEvent): PushEventRead {
        val pushEvent = pushEventRepository.findById(command.pushEventId)
            .orElseThrow { GuamNotFound("PUSH_EVENT [${command.pushEventId}] NOT FOUND") }

        if (pushEvent.userId != command.userId) {
            throw GuamUnAuthorized("USER [${command.userId}] can't update PUSH_EVENT [${command.pushEventId}]")
        }

        pushEvent.isRead = true

        return PushEventRead(command.userId, command.pushEventId)
    }
}

data class ReadPushEvent(
    val userId: Long,
    val pushEventId: Long,
) : Command

data class PushEventRead(
    val userId: Long,
    val pushEventId: Long,
) : Result
