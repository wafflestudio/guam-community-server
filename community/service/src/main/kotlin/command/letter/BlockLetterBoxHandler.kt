package waffle.guam.community.service.command.letter

import org.hibernate.StaleStateException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.service.LetterBoxId
import waffle.guam.community.service.LetterNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class BlockLetterBoxHandler(
    private val letterBoxRepository: LetterBoxRepository,
) : CommandHandler<BlockLetterBox, LetterBoxBlocked> {
    @Retryable(value = [StaleStateException::class, OptimisticLockingFailureException::class])
    @Transactional
    override fun handle(command: BlockLetterBox): LetterBoxBlocked {
        val letterBox = letterBoxRepository.findByIdOrNull(command.letterBoxId) ?: throw LetterNotFound()
        letterBox.blockOther(command.userId)

        return LetterBoxBlocked(command)
    }
}

data class BlockLetterBox(
    val userId: UserId,
    val letterBoxId: LetterBoxId,
) : Command

data class LetterBoxBlocked(
    val userId: UserId,
    val letterBoxId: LetterBoxId,
) : Result

fun LetterBoxBlocked(command: BlockLetterBox): LetterBoxBlocked =
    LetterBoxBlocked(
        command.userId,
        command.letterBoxId,
    )
