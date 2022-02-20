package waffle.guam.community.service.command.letter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.GuamForbidden
import waffle.guam.community.common.LetterNotFound
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeleteLetterHandler(
    private val letterRepository: LetterRepository,
) : CommandHandler<DeleteLetter, LetterDeleted> {
    @Transactional
    override fun handle(command: DeleteLetter): LetterDeleted {
        val letter = letterRepository.findByIdOrNull(command.letterId) ?: throw LetterNotFound(command.letterId)
        letter.deleteBy(command.userId)
        return LetterDeleted(letter.id)
    }

    private fun LetterEntity.deleteBy(userId: UserId) {
        require(userId == this.userId) { throw GuamForbidden("쪽지를 삭제할 권한이 없습니다.") }
        status = LetterEntity.Status.DELETED
    }
}

data class DeleteLetter(
    val userId: UserId,
    val letterId: LetterId,
) : Command

data class LetterDeleted(
    val letterId: LetterId,
) : Result
