package waffle.guam.community.service.command.user.stack

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.stack.StackEntity
import waffle.guam.community.data.jdbc.user.UserAPIRepository
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreateUserStackHandler(
    private val userAPIRepository: UserAPIRepository,
) : CommandHandler<CreateUserStack, UserStackCreated> {

    @Transactional
    override fun handle(command: CreateUserStack): UserStackCreated {
        val (userId, name) = command
        val user = userAPIRepository.find(userId, fetchStacks = true) ?: throw UserNotFound(userId)
        user.stacks.add(StackEntity(userId, name))
        return UserStackCreated(userId, name)
    }
}

data class CreateUserStack(
    val userId: Long,
    val name: String,
) : Command

data class UserStackCreated(
    val userId: Long,
    val name: String,
) : Result
