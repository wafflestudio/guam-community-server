package waffle.guam.letter.data.r2dbc.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Table("letters")
data class LetterEntity(
    @Id
    val id: Long = 0L,
    val letterBoxId: Long,
    val sentBy: Long,
    val sentTo: Long,
    val text: String?,
    val imagePath: String?,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))
)
