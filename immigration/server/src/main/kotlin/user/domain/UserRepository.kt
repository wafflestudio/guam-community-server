package waffle.guam.immigration.server.user.domain

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long> {
    suspend fun findByFirebaseId(fUid: String): User?
}
