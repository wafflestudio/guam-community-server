package waffle.guam.community.service.command.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.domain.user.User

@Service
class CreateUserHandler(
    private val userRepository: UserRepository
) : CommandHandler<CreateUser, UserCreated> {

    @Transactional
    override fun handle(command: CreateUser): UserCreated =
        userRepository.save(UserEntity(id = command.immigrationId)).let(::UserCreated)
}

data class CreateUser(
    val immigrationId: Long,
) : Command

data class UserCreated(
    val user: User,
) : Result

private fun UserCreated(e: UserEntity): UserCreated {
    return UserCreated(User(e))
}
