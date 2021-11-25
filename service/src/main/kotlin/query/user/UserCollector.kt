package waffle.guam.community.service.query.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserQueryGenerator
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class UserCollector(
    private val userRepository: UserRepository,
) : MultiCollector<User, UserId>, UserQueryGenerator {
    override fun get(id: UserId): User =
        userRepository.findByIdOrNull(id)
            ?.toUser()
            ?: throw Exception("USER NOT FOUND ($id)")

    override fun multiGet(ids: Collection<UserId>): Map<UserId, User> =
        userRepository.findAllById(ids)
            .also { it.throwIfNotContainIds(ids) }
            .map { it.id to it.toUser() }
            .toMap()

    private fun UserEntity.toUser() = User(
        id = id,
        firebaseUid = firebaseUid,
        username = username
    )

    fun Collection<UserEntity>.throwIfNotContainIds(ids: Collection<Long>) = apply {
        val missed = ids - map { it.id }

        if (missed.isNotEmpty()) {
            throw Exception("USER NOT FOUND $missed")
        }
    }

    @Service
    class CacheImpl(
        userRepository: UserRepository,
    ) : UserCollector(userRepository) {
        private val cache = Cache<User, UserId>(
            maximumSize = 2000,
            duration = Duration.ofMinutes(5),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(id: UserId): User = cache.get(id)

        override fun multiGet(ids: Collection<UserId>): Map<UserId, User> = cache.multiGet(ids)
    }
}
