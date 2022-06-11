package waffle.guam.user.service.auth

import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.UserEntity
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.service.Command
import waffle.guam.user.service.DuplicateUser
import waffle.guam.user.service.auth.AuthCommandService.CreateUser
import waffle.guam.user.service.user.User

interface AuthCommandService {
    fun createUser(command: CreateUser): User

    data class CreateUser(val firebaseId: String) : Command
}

@Service
class AuthCommandServiceImpl(
    private val userRepository: UserRepository,
) : AuthCommandService {
    override fun createUser(command: CreateUser): User {
        userRepository.findByFirebaseId(command.firebaseId)?.let { throw DuplicateUser() }

        return userRepository.save(
            UserEntity(firebaseId = command.firebaseId)
        ).let(::User)
    }
}
