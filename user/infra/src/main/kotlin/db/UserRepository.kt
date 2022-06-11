package waffle.guam.user.infra.db

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByFirebaseId(firebaseId: String): UserEntity?
}
