package waffle.guam.community.data.jdbc.letter

import org.springframework.data.jpa.domain.Specification
import waffle.guam.community.data.jdbc.QueryGenerator

interface LetterQueryGenerator : QueryGenerator<LetterEntity> {
    fun letterId(id: Long): Specification<LetterEntity> = eq(LetterEntity_.ID, id)

    fun userId(userId: Long): Specification<LetterEntity> = eq(LetterEntity_.USER_ID, userId)

    fun sentTo(userId: Long): Specification<LetterEntity> = eq(LetterEntity_.SENT_TO, userId)

    fun sentBy(userId: Long): Specification<LetterEntity> = eq(LetterEntity_.SENT_BY, userId)

    fun status(status: LetterEntity.Status): Specification<LetterEntity> = eq(LetterEntity_.STATUS, status)

    fun statusIn(statuses: Collection<LetterEntity.Status>): Specification<LetterEntity> = `in`(LetterEntity_.STATUS, statuses)
}
