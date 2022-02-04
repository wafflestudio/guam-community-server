package waffle.guam.community.controller.report

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.report.req.ReportRequest
import waffle.guam.community.service.command.report.CreateReport
import waffle.guam.community.service.command.report.CreateReportHandler

@RequestMapping("/api/v1/report")
@RestController
class ReportController(
    private val createReportHandler: CreateReportHandler,
) {
    @PostMapping("")
    fun report(
        userContext: UserContext,
        @RequestBody request: ReportRequest,
    ) = createReportHandler.handle(
        CreateReport(
            reporterId = userContext.id,
            suspectId = request.suspectId,
            reportType = request.type,
        )
    )
}
