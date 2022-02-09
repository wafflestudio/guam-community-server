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
import waffle.guam.community.controller.user.req.AddUserInterestRequest
import waffle.guam.community.controller.user.req.UpdateDeviceTokenRequest
import waffle.guam.community.controller.user.req.UpdateUserRequest
import waffle.guam.community.service.command.user.UpdateUserHandler
import waffle.guam.community.service.command.user.interest.CreateUserInterestHandler
import waffle.guam.community.service.command.user.interest.DeleteUserInterest
import waffle.guam.community.service.command.user.interest.DeleteUserInterestHandler
import waffle.guam.community.service.query.post.displayer.PostDisplayer
import waffle.guam.community.service.query.user.displayer.UserDisplayer

@RequestMapping("api/v1/users")
@RestController
class UserController(
    private val updateUserHandler: UpdateUserHandler,
    private val createUserInterestHandler: CreateUserInterestHandler,
    private val deleteUserInterestHandler: DeleteUserInterestHandler,
    private val userDisplay: UserDisplayer,
    private val postDisplay: PostDisplayer,
) {
    @GetMapping("/me")
    fun userMe(
        userContext: UserContext,
    ) = userDisplay.getUser(userContext.id)

    @PatchMapping("/deviceToken")
    fun userUpdate(
        userContext: UserContext,
        @RequestBody request: UpdateDeviceTokenRequest,
    ) = updateUserHandler.updateToken(userContext.id, request.deviceToken)

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

    @PostMapping("{userId}/interest")
    fun addInterest(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestBody request: AddUserInterestRequest,
    ) = createUserInterestHandler.handle(request.toCommand(userId))

    @DeleteMapping("{userId}/interest")
    fun deleteInterest(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = true) name: String,
    ) = deleteUserInterestHandler.handle(DeleteUserInterest(userId, name))

    @GetMapping("{userId}/posts")
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
