package waffle.guam.community.service.query.user

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserQueryGenerator
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.user.UserUpdated
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
            ?.let(::User)
            ?: throw Exception("USER NOT FOUND ($id)")

    override fun multiGet(ids: Collection<UserId>): Map<UserId, User> =
        userRepository.findAllById(ids)
            .also { it.throwIfNotContainIds(ids) }
            .associate { it.id to User(it) }

    fun Collection<UserEntity>.throwIfNotContainIds(ids: Collection<Long>) = apply {
        val missed = ids - map { it.id }.toSet()

        if (missed.isNotEmpty()) {
            throw Exception("USER NOT FOUND $missed")
        }
    }

    @Service
    class CacheImpl(
        userRepository: UserRepository,
    ) : UserCollector(userRepository) {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache<User, UserId>(
            maximumSize = 2000,
            duration = Duration.ofMinutes(5),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(id: UserId): User = cache.get(id)

        override fun multiGet(ids: Collection<UserId>): Map<UserId, User> = cache.multiGet(ids)

        @EventListener
        fun reload(userUpdated: UserUpdated) {
            cache.reload(userUpdated.userId)
            logger.info("Cache reloaded with $userUpdated")
        }
    }
}