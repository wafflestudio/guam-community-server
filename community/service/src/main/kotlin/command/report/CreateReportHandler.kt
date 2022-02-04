package waffle.guam.community.service.command.report

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.report.ReportEntity
import waffle.guam.community.data.jdbc.report.ReportRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreateReportHandler(
    private val reportRepository: ReportRepository,
) : CommandHandler<CreateReport, ReportCreated> {
    override fun handle(command: CreateReport): ReportCreated {
        val report = reportRepository.save(
            ReportEntity(
                reporterId = command.reporterId,
                suspectId = command.suspectId,
                reportType = command.reportType
            )
        )
        return ReportCreated(report.reporterId, report.suspectId, report.reportType)
    }
}

data class CreateReport(
    val reporterId: Long,
    val suspectId: Long,
    val reportType: ReportEntity.Type,
) : Command

data class ReportCreated(
    val reporterId: Long,
    val suspectId: Long,
    val reportType: ReportEntity.Type,
) : Result
