package waffle.guam.community.service.domain.letter

import waffle.guam.community.data.jdbc.letter.LetterBoxEntity
import waffle.guam.community.data.jdbc.letter.LetterEntity

data class LetterBox(
    val id: Long,
    val latestLetter: Letter,
    val isLastUnread: Boolean,
)

fun LetterBox(letterBoxEntity: LetterBoxEntity, latestLetter: LetterEntity, userId: Long): LetterBox {
    val lastReadId = letterBoxEntity.lastReadLetterIdOf(userId)
    val isLastUnread = (latestLetter.senderId != userId && latestLetter.id != lastReadId)
    return LetterBox(
        id = letterBoxEntity.id,
        latestLetter = Letter(latestLetter),
        isLastUnread = isLastUnread,
    )
}
