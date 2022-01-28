package waffle.guam.community.service.command.letter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.service.Forbidden
import waffle.guam.community.service.LetterNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeleteLetterHandler(
    private val letterRepository: LetterRepository,
    private val letterBoxRepository: LetterBoxRepository,
) : CommandHandler<DeleteLetter, LetterDeleted> {

    @Transactional
    override fun handle(command: DeleteLetter): LetterDeleted {
        val (letterId, userId) = command
        val letter = letterRepository.findByIdOrNull(letterId) ?: throw LetterNotFound(letterId)
        letter.deleteBy(userId)
        return LetterDeleted(letterId, userId)
    }

    private fun LetterEntity.deleteBy(userId: Long) {
        if (receiverId != userId) throw Forbidden("쪽지를 삭제할 권한이 없습니다.")
        status = LetterEntity.Status.DELETED
    }
}

data class DeleteLetter(
    val letterId: Long,
    val userId: Long,
) : Command

data class LetterDeleted(
    val letterId: Long,
    val userId: Long,
) : Result
