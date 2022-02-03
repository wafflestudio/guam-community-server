package waffle.guam.community.controller.report

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.report.req.ReportRequest
import waffle.guam.community.service.command.letter.ReportLetterBox
import waffle.guam.community.service.command.letter.ReportLetterBoxHandler

@RequestMapping("/api/v1/report")
@RestController
class ReportController(
    private val reportLetterBoxHandler: ReportLetterBoxHandler,
) {
    @PostMapping("")
    fun report(
        userContext: UserContext,
        @RequestBody request: ReportRequest,
    ) = reportLetterBoxHandler.handle(
        ReportLetterBox(
            reporterId = userContext.id,
            suspectId = request.suspectId,
            letterBoxId = request.letterBoxId,
            reportType = request.type,
        )
    )
}
