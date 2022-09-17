package waffle.guam.community.controller.report

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.UserContext
import waffle.guam.community.service.command.report.CreateCommentReportHandler
import waffle.guam.community.service.command.report.CreatePostReportHandler

@RestController
@RequestMapping("/api/v1")
class ReportController(
    private val postHandler: CreatePostReportHandler,
    private val commentHandler: CreateCommentReportHandler,
) {
    @PostMapping("/posts/report")
    fun report(
        userContext: UserContext,
        @RequestBody request: ReportRequest
    ) = request.run {
        postHandler.handle(request.toCommand(userContext.id))
    }

    @PostMapping("/comments/report")
    fun report(
        userContext: UserContext,
        @RequestBody request: ReportCommentRequest
    ) = request.run {
        commentHandler.handle(request.toCommand(userContext.id))
    }
}
