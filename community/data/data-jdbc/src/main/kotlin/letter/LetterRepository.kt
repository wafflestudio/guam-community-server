package waffle.guam.community.data.jdbc.letter

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import waffle.guam.community.data.jdbc.letter.QLetterEntity.letterEntity

interface LetterRepository : JpaRepository<LetterEntity, Long>, LetterRepositoryCustom

interface LetterRepositoryCustom {
    fun findLetterBoxes(userId: Long): List<LetterRepositoryImpl.GroupedLetters>
    fun find(userId: Long, pairId: Long, lastLetterId: Long, size: Long): List<LetterEntity>
}

class LetterRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : LetterRepositoryCustom {
    override fun findLetterBoxes(userId: Long): List<GroupedLetters> =
        queryFactory
            .select(
                Projections.constructor(
                    GroupedLetters::class.java,
                    letterEntity.id.max(), letterEntity.pairId
                )
            )
            .from(letterEntity)
            .where(
                letterEntity.senderId.eq(userId).or(letterEntity.receiverId.eq(userId))
            )
            .groupBy(letterEntity.pairId)
            .fetch()

    override fun find(userId: Long, pairId: Long, lastLetterId: Long, size: Long): List<LetterEntity> =
        queryFactory
            .selectFrom(letterEntity)
            .where(
                letterEntity.pairId.eq(getPairIdOf(userId, pairId)),
                letterEntity.id.gt(lastLetterId),
            )
            .orderBy(letterEntity.id.desc())
            .limit(size)
            .fetch()

    data class GroupedLetters(
        val latestLetterId: Long,
        val pairId: String,
    )
}
