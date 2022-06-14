package waffle.guam.user.service.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.user.infra.aws.ImageClient
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.service.Command
import waffle.guam.user.service.CommandService
import waffle.guam.user.service.DuplicateInterest
import waffle.guam.user.service.InterestNotFound
import waffle.guam.user.service.UserNotFound
import waffle.guam.user.service.user.UserCommandService.CreateInterest
import waffle.guam.user.service.user.UserCommandService.DeleteInterest
import waffle.guam.user.service.user.UserCommandService.UpdateUser

interface UserCommandService : CommandService {
    fun updateUser(command: UpdateUser): User
    fun createInterest(command: CreateInterest): User
    fun deleteInterest(command: DeleteInterest): User

    data class UpdateUser(
        val userId: Long,
        val nickname: String?,
        val introduction: String?,
        val githubId: String?,
        val blogUrl: String?,
        val profileImage: MultipartFile? = null,
        val clearImage: Boolean = false,
    ) : Command

    data class CreateInterest(
        val userId: Long,
        val interest: Interest,
    ) : Command

    data class DeleteInterest(
        val userId: Long,
        val interest: Interest,
    ) : Command
}

@Transactional
@Service
class UserCommandServiceImpl(
    private val userRepository: UserRepository,
    private val imageClient: ImageClient,
) : UserCommandService {

    override fun updateUser(command: UpdateUser): User {
        val user = userRepository.findById(command.userId).orElseThrow(::UserNotFound)

        user.let {
            it.nickname = command.nickname ?: it.nickname
            it.introduction = command.introduction ?: it.introduction
            it.githubId = command.githubId ?: it.githubId
            it.blogUrl = command.blogUrl ?: it.blogUrl
        }

        if (command.clearImage) {
            user.profileImage = null
        } else if (command.profileImage != null) {
            user.profileImage =
                imageClient.upload(command.userId, command.profileImage).path.substring(1) // '/' 제거해서 저장
        }

        return User(user)
    }

    override fun createInterest(command: CreateInterest): User {
        val user = userRepository.findById(command.userId).orElseThrow(::UserNotFound)

        if (command.interest.name in user.interests) {
            throw DuplicateInterest()
        }

        user.interests.add(command.interest.name)

        return User(user)
    }

    override fun deleteInterest(command: DeleteInterest): User {
        val user = userRepository.findById(command.userId).orElseThrow(::UserNotFound)

        if (command.interest.name !in user.interests) {
            throw InterestNotFound()
        }

        user.interests.remove(command.interest.name)

        return User(user)
    }
}
