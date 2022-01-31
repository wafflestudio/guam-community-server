package waffle.guam.community.data.jdbc.letter

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import waffle.guam.community.data.jdbc.letter.QLetterEntity.letterEntity

interface LetterRepository : JpaRepository<LetterEntity, Long>, LetterRepositoryCustom

interface LetterRepositoryCustom {
    fun find(letterBoxId: Long, lastLetterId: Long, size: Long): List<LetterEntity>
}

class LetterRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : LetterRepositoryCustom {

    override fun find(letterBoxId: Long, lastLetterId: Long, size: Long): List<LetterEntity> =
        queryFactory
            .selectFrom(letterEntity)
            .where(
                letterEntity.letterBoxId.eq(letterBoxId),
                letterEntity.id.gt(lastLetterId),
            )
            .orderBy(letterEntity.id.desc())
            .limit(size)
            .fetch()
}
