package waffle.guam.immigration.app.user

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.immigration.api.user.GetUserRequest
import waffle.guam.immigration.api.user.GetUserResponse
import waffle.guam.immigration.api.user.UserService

@RequestMapping("/api/v1/user")
@RestController
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    suspend fun getUser(request: GetUserRequest): GetUserResponse =
        userService.getUser(request)
}