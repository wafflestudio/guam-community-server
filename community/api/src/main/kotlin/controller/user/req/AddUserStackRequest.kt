package waffle.guam.community.controller.user.req

import waffle.guam.community.service.command.user.stack.CreateUserStack

class AddUserStackRequest(
    val name: String
) {
    fun toCommand(userId: Long) =
        CreateUserStack(userId, name)
}
