package waffle.guam.community.service.command.report

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.data.jdbc.report.PostCommentReportEntity
import waffle.guam.community.data.jdbc.report.PostReportEntity
import waffle.guam.community.data.jdbc.report.ReportRepository
import waffle.guam.community.service.GuamForbidden
import waffle.guam.community.service.PostCommentNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.EventResult

@Service
class CreateCommentReportHandler(
    private val repository: ReportRepository,
    private val postCommentRepository: PostCommentRepository,
): CommandHandler<CreatePostCommentReport, ReportCreated> {

    @Transactional
    override fun handle(command: CreatePostCommentReport): ReportCreated {
        val post = postCommentRepository.findByIdOrNull(command.commentId) ?: throw PostCommentNotFound(command.commentId)

        if(post.userId == command.userId) {
            throw GuamForbidden("본인이 작성한 댓글은 신고할 수 없어요.")
        }

        return ReportCreated(repository.save(command.toEntity()).id)
    }

    private fun CreatePostCommentReport.toEntity() = PostCommentReportEntity(commentId, userId, reason)
}

data class CreatePostCommentReport(
    val commentId: Long,
    val userId: Long,
    val reason: String,
): Command
