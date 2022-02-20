package waffle.guam.community.data.jdbc.user

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import waffle.guam.community.common.UserNotFound
import waffle.guam.community.data.jdbc.user.QUserEntity.userEntity

@Repository
class UserApiRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun find(id: Long, fetchInterests: Boolean = true): UserEntity =
        queryFactory
            .select(userEntity)
            .from(userEntity)
            .where(eqId(id))
            .fetchJoinIf(fetchInterests)
            .fetchOne()
            ?: throw UserNotFound(id)

    fun findAll(ids: Collection<Long>, fetchInterests: Boolean = true): List<UserEntity> {
        return if (ids.isEmpty()) listOf()
        else queryFactory
            .select(userEntity)
            .from(userEntity)
            .fetchJoinIf(fetchInterests)
            .where(
                userEntity.id.`in`(ids)
            )
            .fetch()
    }

    private fun eqId(id: Long?) =
        id?.run { userEntity.id.eq(this) }

    private fun JPAQuery<UserEntity>.fetchJoinIf(flag: Boolean) =
        if (flag) this.leftJoin(userEntity.interests).fetchJoin()
        else this
}
