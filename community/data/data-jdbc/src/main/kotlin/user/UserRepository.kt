package waffle.guam.community.data.jdbc.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findAll(spec: Specification<UserEntity>, pageable: Pageable): Page<UserEntity>
    fun findOne(spec: Specification<UserEntity>): UserEntity?
}
