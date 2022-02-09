package waffle.guam.community.controller.user.req

import waffle.guam.community.service.command.user.interest.CreateUserInterest

class AddUserInterestRequest(
    val name: String
) {
    fun toCommand(userId: Long) =
        CreateUserInterest(userId, name)
}
