package waffle.guam.community.service.command.report

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.report.PostCommentReportEntity
import waffle.guam.community.data.jdbc.report.PostReportEntity
import waffle.guam.community.data.jdbc.report.ReportRepository
import waffle.guam.community.service.GuamForbidden
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.EventResult

@Service
class CreatePostReportHandler(
    private val repository: ReportRepository,
    private val postRepository: PostRepository,
): CommandHandler<CreatePostReport, ReportCreated> {

    @Transactional
    override fun handle(command: CreatePostReport): ReportCreated {
        val post = postRepository.findByIdOrNull(command.postId) ?: throw PostNotFound(command.postId)

        if(post.userId == command.userId) {
            throw GuamForbidden("본인이 작성한 게시글은 신고할 수 없어요.")
        }

        return ReportCreated(repository.save(command.toEntity()).id)
    }

    private fun CreatePostReport.toEntity() = PostReportEntity(postId, userId, reason)
}

data class CreatePostReport(
    val postId: Long,
    val userId: Long,
    val reason: String,
): Command

data class ReportCreated(
    val reportId: Long,
): EventResult
