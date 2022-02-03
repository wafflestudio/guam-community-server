package waffle.guam.community.service.command.letter

import org.hibernate.StaleStateException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.service.LetterBoxId
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.LetterNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class UpdateLetterBoxHandler(
    private val letterBoxRepository: LetterBoxRepository,
) : CommandHandler<UpdateLetterBox, LetterBoxUpdated> {
    @Retryable(value = [StaleStateException::class, OptimisticLockingFailureException::class])
    @Transactional
    override fun handle(command: UpdateLetterBox): LetterBoxUpdated {
        val letterBox = letterBoxRepository.findByIdOrNull(command.letterBoxId) ?: throw LetterNotFound("쪽지함을 찾을 수 없습니다.")
        letterBox.setLastReadLetterIdOf(command.userId, command.lastReadLetterId)
        return LetterBoxUpdated(command.userId, command.letterBoxId, command.lastReadLetterId)
    }
}

data class UpdateLetterBox(
    val userId: UserId,
    val letterBoxId: LetterBoxId,
    val lastReadLetterId: LetterId,
) : Command

data class LetterBoxUpdated(
    val userId: UserId,
    val letterBoxId: LetterBoxId,
    val lastReadLetterId: LetterId,
) : Result
