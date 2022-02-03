package waffle.guam.community.service.command.letter

import org.hibernate.StaleStateException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.data.jdbc.report.ReportEntity
import waffle.guam.community.data.jdbc.report.ReportRepository
import waffle.guam.community.service.LetterBoxId
import waffle.guam.community.service.LetterNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class ReportLetterBoxHandler(
    private val letterBoxRepository: LetterBoxRepository,
    private val reportRepository: ReportRepository,
) : CommandHandler<ReportLetterBox, LetterBoxReported> {
    @Retryable(value = [StaleStateException::class, OptimisticLockingFailureException::class])
    @Transactional
    override fun handle(command: ReportLetterBox): LetterBoxReported {
        val letterBox = letterBoxRepository.findByIdOrNull(command.letterBoxId) ?: throw LetterNotFound()
        letterBox.report(command.reporterId)

        reportRepository.save(command.toEntity())
        return LetterBoxReported(command)
    }

    private fun ReportLetterBox.toEntity() =
        ReportEntity(reporterId, suspectId, letterBoxId, reportType)
}

data class ReportLetterBox(
    val reporterId: UserId,
    val suspectId: UserId,
    val letterBoxId: LetterBoxId,
    val reportType: ReportEntity.Type,
) : Command

data class LetterBoxReported(
    val reporterId: UserId,
    val suspectId: UserId,
    val letterBoxId: LetterBoxId,
    val reportType: ReportEntity.Type,
) : Result

fun LetterBoxReported(command: ReportLetterBox): LetterBoxReported =
    LetterBoxReported(
        command.reporterId,
        command.suspectId,
        command.letterBoxId,
        command.reportType,
    )
