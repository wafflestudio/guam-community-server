package waffle.guam.community.controller.report

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.UserContext
import waffle.guam.community.service.command.report.CreateReport
import waffle.guam.community.service.command.report.CreateReportHandler

@RestController
@RequestMapping("/api/v1/posts/report")
class ReportController(
    private val reportCommandHandler: CreateReportHandler,
) {
    @PostMapping
    fun report(
        userContext: UserContext,
        @RequestBody request: ReportRequest
    ) = request.run {
        reportCommandHandler.handle(CreateReport(postId, userContext.id, reason))
    }
}
