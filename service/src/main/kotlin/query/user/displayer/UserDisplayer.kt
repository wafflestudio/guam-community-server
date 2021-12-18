package waffle.guam.community.service.query.user.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.comment.PostCommentAPIRepository
import waffle.guam.community.service.domain.comment.MyCommentView
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.user.UserCollector

@Service
class UserDisplayer(
    private val userCollector: UserCollector,
    private val commentAPIRepository: PostCommentAPIRepository,
) {
    fun getUser(userId: Long): User =
        userCollector.get(userId)

    fun getUserComments(userId: Long, afterCommentId: Long?, sortByLikes: Boolean): List<MyCommentView> {
        val data = commentAPIRepository.findCommentsOfUser(userId, afterCommentId)
        return MyCommentView.listOf(data)
    }
}
