package waffle.guam.community.service.query.user.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.common.UserNotFound
import waffle.guam.community.data.jdbc.comment.PostCommentAPIRepository
import waffle.guam.community.service.command.user.CreateUser
import waffle.guam.community.service.command.user.CreateUserHandler
import waffle.guam.community.service.domain.comment.MyCommentView
import waffle.guam.community.service.domain.comment.MyCommentViewList
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.user.UserCollector

@Service
class UserDisplayer(
    private val createUserHandler: CreateUserHandler,
    private val userCollector: UserCollector,
    private val commentAPIRepository: PostCommentAPIRepository,
) {
    fun getUser(userId: Long): User = try {
        userCollector.get(userId)
    } catch (e: UserNotFound) {
        createUserHandler.handle(CreateUser(immigrationId = userId)).user
    }

    fun getUserComments(userId: Long, beforeCommentId: Long?, sortByLikes: Boolean): List<MyCommentView> {
        val data = commentAPIRepository.findCommentsOfUser(userId, beforeCommentId)
        return MyCommentViewList(data)
    }
}
