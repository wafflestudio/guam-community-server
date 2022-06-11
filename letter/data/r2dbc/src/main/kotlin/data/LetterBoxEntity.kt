package waffle.guam.letter.data.r2dbc.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.lang.Long.max
import java.lang.Long.min

@Table("letter_boxes")
data class LetterBoxEntity(
    @Id
    val id: Long = 0L,
    val lowId: Long,
    val highId: Long,
    val lowDeleteMarkedId: Long? = null,
    val highDeleteMarkedId: Long? = null,
    @Transient
    val letters: List<LetterEntity>? = null,
) {
    @PersistenceConstructor
    constructor(
        id: Long,
        lowId: Long,
        highId: Long,
        lowDeleteMarkedId: Long? = null,
        highDeleteMarkedId: Long? = null,
    ) : this(id, lowId, highId, lowDeleteMarkedId, highDeleteMarkedId, null)

    constructor(userId: Long, pairId: Long) : this(lowId = min(userId, pairId), highId = max(userId, pairId))
}

fun LetterBoxEntity.pairId(userId: Long) = when (userId) {
    lowId -> highId
    highId -> lowId
    else -> throw RuntimeException("")
}

fun LetterBoxEntity.deleteMarkedId(userId: Long) = when (userId) {
    lowId -> lowDeleteMarkedId
    highId -> highDeleteMarkedId
    else -> throw RuntimeException("")
}
