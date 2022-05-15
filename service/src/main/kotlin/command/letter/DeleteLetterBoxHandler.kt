package waffle.guam.community.service.command.letter

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.GuamForbidden
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterQueryGenerator
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeleteLetterBoxHandler(
    private val letterRepository: LetterRepository,
) : CommandHandler<DeleteLetterBox, LetterDeleted>, LetterQueryGenerator {
    @Transactional
    override fun handle(command: DeleteLetterBox): LetterDeleted {
        val (userId, pairId) = command
        val lettersSent = letterRepository.findAll(spec = userId(userId) * status(LetterEntity.Status.ACTIVE) * sentTo(pairId))
        val lettersGot = letterRepository.findAll(spec = userId(userId) * status(LetterEntity.Status.ACTIVE) * sentBy(pairId))

        return (lettersSent + lettersGot)
            .map { letter -> letter.deleteBy(userId); letter.id }
            .let { letterIds -> LetterDeleted(userId, pairId, letterIds) }
    }

    private fun LetterEntity.deleteBy(userId: UserId) {
        require(userId == this.userId) { throw GuamForbidden("쪽지를 삭제할 권한이 없습니다.") }
        status = LetterEntity.Status.DELETED
    }
}

data class DeleteLetterBox(
    val userId: UserId,
    val pairId: UserId,
) : Command

data class LetterDeleted(
    val userId: UserId,
    val pairId: UserId,
    val letterIds: List<LetterId>,
) : Result
