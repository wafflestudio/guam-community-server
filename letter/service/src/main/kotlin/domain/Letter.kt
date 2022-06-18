package waffle.guam.favorite.service.domain

import waffle.guam.letter.data.r2dbc.data.LetterEntity
import java.time.Instant
import java.time.ZoneOffset

data class Letter(
    val id: Long,
    val letterBoxId: Long,
    val sentBy: Long,
    val sentTo: Long,
    val text: String?,
    val imagePath: String?,
    val isRead: Boolean,
    val createdAt: Instant,
)

fun LetterEntity.toDomain(): Letter = Letter(
    id = id,
    letterBoxId = letterBoxId,
    sentBy = sentBy,
    sentTo = sentTo,
    text = text,
    imagePath = imagePath,
    isRead = isRead,
    createdAt = createdAt.toInstant(ZoneOffset.UTC),
)

fun Letter.setReadBy(userId: Long): Letter =
    if (sentTo == userId) {
        copy(isRead = true)
    } else {
        this
    }
