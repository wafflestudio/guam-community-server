package waffle.guam.community.data.jdbc.letter

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import waffle.guam.community.data.jdbc.letter.QLetterBoxEntity.letterBoxEntity
import waffle.guam.community.data.jdbc.letter.QLetterEntity.letterEntity

interface LetterBoxRepository : JpaRepository<LetterBoxEntity, Long>, LetterBoxRepositoryCustom

interface LetterBoxRepositoryCustom {
    fun find(senderId: Long, receiverId: Long): LetterBoxEntity?
    fun findBoxIds(userId: Long): List<Long>
    fun findPreviews(boxIds: List<Long>): List<LetterBoxPreview>
}

class LetterBoxRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : LetterBoxRepositoryCustom {
    override fun find(senderId: Long, receiverId: Long): LetterBoxEntity? {
        val (lowUserId, highUserId) = setOf(senderId, receiverId).sorted()
        return queryFactory
            .selectFrom(letterBoxEntity)
            .where(
                letterBoxEntity.lowUserId.eq(lowUserId),
                letterBoxEntity.highUserId.eq(highUserId),
            )
            .fetchOne()
    }

    override fun findBoxIds(userId: Long): List<Long> =
        queryFactory
            .select(letterBoxEntity.id)
            .from(letterBoxEntity)
            .where(
                letterBoxEntity.lowUserId.eq(userId)
                    .or(letterBoxEntity.highUserId.eq(userId))
            )
            .fetch()

    // 서브쿼리가 의도대로 작동을 안해서 쿼리 두개로 분리.. ㅜㅜ
    override fun findPreviews(boxIds: List<Long>): List<LetterBoxPreview> {
        if (boxIds.isEmpty()) {
            return listOf()
        } else {
            val boxes = queryFactory
                .selectFrom(letterBoxEntity)
                .where(letterBoxEntity.id.`in`(boxIds))
                .fetch()
            val letterAlias = QLetterEntity("alias")
            val letters = queryFactory
                .select(letterEntity)
                .from(letterEntity)
                .leftJoin(letterAlias)
                .on(
                    letterEntity.letterBoxId.eq(letterAlias.letterBoxId),
                    letterEntity.id.lt(letterAlias.id)
                )
                .where(
                    letterAlias.isNull,
                    letterEntity.letterBoxId.`in`(boxIds),
                )
                .fetch()
                .associateBy { it.letterBoxId }
            return boxes.map { letterBox ->
                LetterBoxPreview(letterBox, letters[letterBox.id]!!)
            }
        }
    }
}

data class LetterBoxPreview(
    val letterBox: LetterBoxEntity,
    val latestLetter: LetterEntity,
)
