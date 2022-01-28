package waffle.guam.community.service.command.letter

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.letter.LetterBoxEntity
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreateLetterBoxHandler(
    private val letterBoxRepository: LetterBoxRepository,
) : CommandHandler<CreateLetterBox, LetterBoxCreated> {
    @Transactional
    override fun handle(command: CreateLetterBox): LetterBoxCreated {
        val entity = letterBoxRepository.save(LetterBoxEntity(senderId = command.senderId, receiverId = command.receiverId))
        return LetterBoxCreated(letterBoxId = entity.id, lowUserId = entity.lowUserId, highUserId = entity.highUserId)
    }
}

data class CreateLetterBox(
    val senderId: Long,
    val receiverId: Long,
) : Command

data class LetterBoxCreated(
    val letterBoxId: Long,
    val lowUserId: Long,
    val highUserId: Long,
) : Result
