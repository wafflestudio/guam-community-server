package waffle.guam.community.service.command.user.interest

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.interest.InterestId
import waffle.guam.community.data.jdbc.interest.InterestRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class DeleteUserInterestHandler(
    private val interestRepository: InterestRepository,
) : CommandHandler<DeleteUserInterest, UserInterestDeleted> {

    @Transactional
    override fun handle(command: DeleteUserInterest): UserInterestDeleted {
        val (userId, name) = command
        interestRepository.deleteById(InterestId(userId, name))
        return UserInterestDeleted(userId, name)
    }
}

data class DeleteUserInterest(
    val userId: Long,
    val name: String,
) : Command

data class UserInterestDeleted(
    val userId: Long,
    val name: String,
) : Result
