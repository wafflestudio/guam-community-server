package waffle.guam.community.controller.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.user.UserUriConstants.USER_DETAIL
import waffle.guam.community.controller.user.UserUriConstants.USER_ME
import waffle.guam.community.controller.user.req.UpdateUserRequest
import waffle.guam.community.service.command.user.UpdateUserHandler
import waffle.guam.community.service.query.user.displayer.UserDisplayer

@RequestMapping
@RestController
class UserController(
    private val updateUserHandler: UpdateUserHandler,
    private val userDisplay: UserDisplayer,
) {
    @GetMapping(USER_ME)
    fun userMe(
        userContext: UserContext,
    ) = userDisplay.getUser(userContext.id)

    @GetMapping(USER_DETAIL)
    fun userDetail(
        userContext: UserContext,
        @PathVariable userId: Long,
    ) = userDisplay.getUser(userId)

    @PatchMapping(USER_DETAIL)
    fun userUpdate(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestBody request: UpdateUserRequest,
    ) = updateUserHandler.handle(request.toCommand(userId))
}
