package waffle.guam.community.controller.comment

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.UserContext
import waffle.guam.community.service.domain.comment.MyCommentView
import waffle.guam.community.service.query.comment.displayer.UserCommentDisplayer

@RestController
class UserPostCommentController(
    private val userCommentDisplay: UserCommentDisplayer,
) {

    @GetMapping("/api/v1/comments/users/{userId}")
    fun userComments(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = false) beforeCommentId: Long?,
        @RequestParam(required = false) sortByLikes: Boolean?,
    ): List<MyCommentView> = userCommentDisplay.getUserComments(userId, beforeCommentId, sortByLikes ?: false)

    @GetMapping("/test")
    fun test() {
    }
}