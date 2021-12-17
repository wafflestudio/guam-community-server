package waffle.guam.community.data.jdbc.user

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import waffle.guam.community.data.jdbc.user.QUserEntity.userEntity

@Repository
class UserAPIRepository(
    private val querydsl: JPAQueryFactory,
) {
    fun find(id: Long, fetchStacks: Boolean = false): UserEntity? =
        querydsl
            .select(userEntity)
            .from(userEntity)
            .where(eqId(id))
            .fetchJoinIf(fetchStacks)
            .fetchOne()

    private fun eqId(id: Long?) =
        id?.run { userEntity.id.eq(this) }

    private fun JPAQuery<UserEntity>.fetchJoinIf(flag: Boolean) =
        if (flag) this.leftJoin(userEntity.stacks).fetchJoin()
        else this
}
