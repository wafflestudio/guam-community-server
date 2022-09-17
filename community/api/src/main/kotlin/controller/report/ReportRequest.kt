package waffle.guam.community.controller.report

import waffle.guam.community.service.command.report.CreatePostCommentReport
import waffle.guam.community.service.command.report.CreatePostReport

data class ReportRequest(
    val postId: Long,
    val reason: String,
) {
    fun toCommand(userId: Long) = CreatePostReport(postId, userId, reason)
}

data class ReportCommentRequest(
    val commentId: Long,
    val reason: String,
) {
    fun toCommand(userId: Long) = CreatePostCommentReport(commentId, userId, reason)
}
