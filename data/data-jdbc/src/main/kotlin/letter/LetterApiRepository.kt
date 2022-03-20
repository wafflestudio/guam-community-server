package waffle.guam.community.data.jdbc.letter

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import waffle.guam.community.data.jdbc.letter.QLetterEntity.letterEntity

@Component
class LetterApiRepository(
    private val queryFactory: JPAQueryFactory,
) {
    /**
     * Fetch All latest letter ids of User's Friends
     * 나와 상대방 중 누가 더 최신인지는 쿼리 레벨에서 구분할 방법이 없다.
     * 인메모리에서 정리해주면 됨
     */
    fun findRecentLetterIdsOf(userId: Long): List<Long> =
        queryFactory
            .select(letterEntity.id.max())
            .from(letterEntity)
            .where(
                letterEntity.userId.eq(userId),
                letterEntity.status.eq(LetterEntity.Status.ACTIVE),
            )
            .groupBy(letterEntity.sentBy, letterEntity.sentTo)
            .fetch()

    /**
     * Fetch All Letters from (user & pair)
     */
    fun findLetters(userId: Long, pairId: Long, beforeLetterId: Long?, size: Long): List<LetterEntity> =
        queryFactory
            .selectFrom(letterEntity)
            .where(
                letterEntity.userId.eq(userId),
                letterEntity.sentBy.eq(pairId).or(letterEntity.sentTo.eq(pairId)),
                letterEntity.status.eq(LetterEntity.Status.ACTIVE),
                beforeLetterId?.let { letterEntity.id.lt(beforeLetterId) },
            )
            .orderBy(letterEntity.id.desc())
            .limit(size)
            .fetch()
}
