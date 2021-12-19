package waffle.guam.community.service.command.user.stack

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.stack.StackId
import waffle.guam.community.data.jdbc.stack.StackRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeleteUserStackHandler(
    private val stackRepository: StackRepository,
) : CommandHandler<DeleteUserStack, UserStackDeleted> {

    @Transactional
    override fun handle(command: DeleteUserStack): UserStackDeleted {
        val (userId, name) = command
        stackRepository.deleteById(StackId(userId, name))
        return UserStackDeleted(userId, name)
    }
}

data class DeleteUserStack(
    val userId: Long,
    val name: String,
) : Command

data class UserStackDeleted(
    val userId: Long,
    val name: String,
) : Result
