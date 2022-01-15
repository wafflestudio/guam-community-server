package waffle.guam.community.controller.user

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.user.req.AddUserStackRequest
import waffle.guam.community.controller.user.req.UpdateUserRequest
import waffle.guam.community.service.command.user.UpdateUserHandler
import waffle.guam.community.service.command.user.stack.CreateUserStackHandler
import waffle.guam.community.service.command.user.stack.DeleteUserStack
import waffle.guam.community.service.command.user.stack.DeleteUserStackHandler
import waffle.guam.community.service.query.post.displayer.PostDisplayer
import waffle.guam.community.service.query.user.displayer.UserDisplayer

@RequestMapping("api/v1/users")
@RestController
class UserController(
    private val updateUserHandler: UpdateUserHandler,
    private val createUserStackHandler: CreateUserStackHandler,
    private val deleteUserStackHandler: DeleteUserStackHandler,
    private val userDisplay: UserDisplayer,
    private val postDisplay: PostDisplayer,
) {
    @GetMapping("/me")
    fun userMe(
        userContext: UserContext,
    ) = userDisplay.getUser(userContext.id)

    @GetMapping("/{userId}")
    fun userDetail(
        userContext: UserContext,
        @PathVariable userId: Long,
    ) = userDisplay.getUser(userId)

    @PatchMapping("/{userId}")
    fun userUpdate(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestBody request: UpdateUserRequest,
    ) = updateUserHandler.handle(request.toCommand(userId))

    @PostMapping("{userId}/stack")
    fun addStack(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestBody request: AddUserStackRequest,
    ) = createUserStackHandler.handle(request.toCommand(userId))

    @DeleteMapping("{userId}/posts")
    fun addStack(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = true) name: String,
    ) = deleteUserStackHandler.handle(DeleteUserStack(userId, name))

    @GetMapping("{userId}/comments")
    fun userPosts(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = false) afterPostId: Long?,
        @RequestParam(required = false) sortByLikes: Boolean?,
    ) = postDisplay.getUserPostList(userId, afterPostId, sortByLikes ?: false)

    @GetMapping("{userId}/comments")
    fun userComments(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = false) afterCommentId: Long?,
        @RequestParam(required = false) sortByLikes: Boolean?,
    ) = userDisplay.getUserComments(userId, afterCommentId, sortByLikes ?: false)
}
