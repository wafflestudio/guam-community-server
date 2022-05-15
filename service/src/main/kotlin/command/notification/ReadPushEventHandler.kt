package waffle.guam.community.service.command.notification

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import waffle.guam.community.common.GuamUnAuthorized
import waffle.guam.community.data.jdbc.push.PushEventRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class ReadPushEventHandler(
    private val pushEventRepository: PushEventRepository,
) : CommandHandler<ReadPushEvents, PushEventsRead> {

    @Transactional
    override fun handle(@RequestBody command: ReadPushEvents): PushEventsRead {
        val (mine, others) = pushEventRepository.findAllById(command.pushEventIds)
            .partition { it.userId == command.userId }

        if (others.isNotEmpty()) {
            throw GuamUnAuthorized("USER [${command.userId}] can't update PUSH_EVENTS [$others]")
        }

        mine.forEach { it.isRead = true }

        return PushEventsRead(command.userId, mine.map { it.id })
    }
}

data class ReadPushEvents(
    val userId: Long,
    val pushEventIds: List<Long>,
) : Command

data class PushEventsRead(
    val userId: Long,
    val pushEventIds: List<Long>,
) : Result
