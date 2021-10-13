package waffle.guam.community.service.query.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class UserCollector(
    private val userRepository: UserRepository,
) : Collector<User, Long> {
    override fun get(id: Long): User =
        userRepository.findByIdOrNull(id)?.let { User.of(it) } ?: throw Exception()

    fun multiGet(ids: Collection<Long>): Map<Long, User> =
        userRepository.findAllById(ids).map { it.id to User.of(it) }.toMap()

    @Service
    class CacheImpl(
        userRepository: UserRepository,
    ) : UserCollector(userRepository) {
        private val cache = Cache<User, Long>(
            maximumSize = 2000,
            duration = Duration.ofMinutes(1),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(id: Long): User = cache.get(id)

        override fun multiGet(ids: Collection<Long>): Map<Long, User> = cache.multiGet(ids)
    }
}
