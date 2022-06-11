package waffle.guam.user.service.user

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.UserRepository
import java.time.Duration

interface UserQueryService {
    fun getUser(userId: Long): User?
    fun getUser(firebaseId: String): User?
    fun getUsers(userIds: List<Long>): List<User>
}

@Primary
@Service
class UserQueryServiceCacheImpl(
    private val impl: UserQueryServiceImpl,
) : UserQueryService {

    private val userIdCache = Caffeine.newBuilder()
        .maximumSize(1000L)
        .expireAfterWrite(Duration.ofDays(1))
        .build<Long, User>()

    private val firebaseCache = Caffeine.newBuilder()
        .maximumSize(1000L)
        .expireAfterWrite(Duration.ofDays(1))
        .build<String, User>()

    override fun getUser(userId: Long): User? {
        val cached = userIdCache.getIfPresent(userId)

        return if (cached != null) {
            cached
        } else {
            impl.getUser(userId)?.also { userIdCache.put(userId, it) }
        }
    }

    override fun getUser(firebaseId: String): User? {
        val cached = firebaseCache.getIfPresent(firebaseId)

        return if (cached != null) {
            cached
        } else {
            impl.getUser(firebaseId)?.also { firebaseCache.put(firebaseId, it) }
        }
    }

    override fun getUsers(userIds: List<Long>): List<User> {
        val cached = userIdCache.getAllPresent(userIds).values
        val missed = run {
            val cachedIds = cached.map { it.id }.toSet()
            val missedIds = userIds.filter { it !in cachedIds }
            impl.getUsers(missedIds)
        }

        userIdCache.putAll(missed.associateBy { it.id })

        return cached + missed
    }
}

@Service
class UserQueryServiceImpl(
    private val userRepository: UserRepository,
) : UserQueryService {

    override fun getUser(userId: Long): User? {
        return userRepository.findById(userId)
            .orElse(null)
            ?.let(::User)
    }

    override fun getUser(firebaseId: String): User? {
        return userRepository.findByFirebaseId(firebaseId)?.let(::User)
    }

    override fun getUsers(userIds: List<Long>): List<User> {
        return userRepository.findAllById(userIds)
            .map(::User)
    }
}
