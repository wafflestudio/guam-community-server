package waffle.guam.letter.data.r2dbc.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Table("blocks")
data class BlockEntity(
    @Id
    val id: Long = 0L,
    val userId: Long,
    val blockUserId: Long,
    val createdAt: LocalDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")),
)
