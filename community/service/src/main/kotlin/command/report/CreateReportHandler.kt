package waffle.guam.community.service.command.report

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.report.ReportEntity
import waffle.guam.community.data.jdbc.report.ReportRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.EventResult

@Service
class CreateReportHandler(
    private val repository: ReportRepository,
): CommandHandler<CreateReport, ReportCreated> {

    @Transactional
    override fun handle(command: CreateReport): ReportCreated {
        return ReportCreated(repository.save(command.toEntity()).id)
    }

    private fun CreateReport.toEntity(): ReportEntity = ReportEntity(postId, userId, reason)
}

data class CreateReport(
    val postId: Long,
    val userId: Long,
    val reason: String,
): Command

data class ReportCreated(
    val reportId: Long,
): EventResult
