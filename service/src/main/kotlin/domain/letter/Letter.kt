package waffle.guam.community.service.domain.letter

import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.UserId
import java.time.Instant

data class Letter(
    val id: LetterId,
    val sentBy: UserId,
    val sentTo: UserId,
    val text: String,
    val createdAt: Instant,
    val imagePath: String?,
    val isRead: Boolean,
)

fun Letter(e: LetterEntity) =
    Letter(
        id = e.id,
        sentBy = e.sentBy,
        sentTo = e.sentTo,
        text = e.text,
        createdAt = e.createdAt,
        imagePath = e.imagePath,
        isRead = e.isRead,
    )
