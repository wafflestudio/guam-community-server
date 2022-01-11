package waffle.guam.community.service.command.user

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreateUserHandler(
    private val userRepository: UserRepository
) : CommandHandler<CreateUser, UserCreated> {

    override fun handle(command: CreateUser): UserCreated {
        TODO("Not yet implemented")
    }
}

data class CreateUser(
    val firebaseUid: String,
    val nickname: String,
    val introduction: String,
    val githubId: String?,
    val blogUrl: String?,
    val stacks: List<String>?,
    val profileImage: MultipartFile?,
) : Command

data class UserCreated(
    val id: Long,
    val firebaseUid: String
) : Result
