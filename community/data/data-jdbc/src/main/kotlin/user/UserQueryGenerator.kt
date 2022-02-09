package waffle.guam.community.data.jdbc.user

import org.springframework.data.jpa.domain.Specification
import waffle.guam.community.data.jdbc.QueryGenerator

interface UserQueryGenerator : QueryGenerator<UserEntity> {

    fun userId(userId: Long): Specification<UserEntity> = eq(UserEntity_.ID, userId)
}
