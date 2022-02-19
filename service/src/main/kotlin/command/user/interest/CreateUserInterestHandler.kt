package waffle.guam.community.service.command.user.interest

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.interest.InterestEntity
import waffle.guam.community.data.jdbc.user.UserAPIRepository
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreateUserInterestHandler(
    private val userAPIRepository: UserAPIRepository,
) : CommandHandler<CreateUserInterest, UserInterestCreated> {

    @Transactional
    override fun handle(command: CreateUserInterest): UserInterestCreated {
        val (userId, name) = command
        val user = userAPIRepository.find(userId, fetchInterests = true) ?: throw UserNotFound(userId)
        user.interests.add(InterestEntity(userId, name))
        return UserInterestCreated(userId, name)
    }
}

data class CreateUserInterest(
    val userId: Long,
    val name: String,
) : Command

data class UserInterestCreated(
    val userId: Long,
    val name: String,
) : Result
