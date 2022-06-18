package waffle.guam.letter.data.r2dbc.repository

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import waffle.guam.letter.data.r2dbc.data.LetterBoxPreviewEntity
import waffle.guam.letter.data.r2dbc.data.LetterEntity
import waffle.guam.letter.data.r2dbc.getBoolean
import waffle.guam.letter.data.r2dbc.getLocalDateTime
import waffle.guam.letter.data.r2dbc.getLong
import waffle.guam.letter.data.r2dbc.getString

interface LetterBoxPreviewRepository {
    suspend fun findAll(userId: Long): List<LetterBoxPreviewEntity>
}

@Service
class LetterBoxPreviewRepositoryImpl(
    private val dbClient: DatabaseClient,
) : LetterBoxPreviewRepository {
    override suspend fun findAll(userId: Long): List<LetterBoxPreviewEntity> {
        return dbClient.sql(
            """
                SELECT lb.*, l.id as l_id, l.sent_by as l_sent_by, l.sent_to as l_sent_to, l.text as l_text, 
                l.created_at as l_created_at, l.image_path as l_image_path, l.is_read as l_is_read 
                FROM (
                    SELECT lb.*, max(l.id) AS letter_id FROM letter_boxes AS lb INNER JOIN letters AS l ON lb.id = l.letter_box_id
                    WHERE lb.low_id = :userId OR lb.high_id = :userId
                    GROUP BY lb.id
                ) AS lb
                INNER JOIN letters AS l on lb.letter_id = l.id
                ORDER BY l_created_at DESC
            """.trimIndent()
        )
            .bind("userId", userId)
            .map { row -> LetterBoxPreview(row) }
            .flow()
            .toList()
    }
}

private fun LetterBoxPreview(row: Row): LetterBoxPreviewEntity {
    return LetterBoxPreviewEntity(
        id = row.getLong("id")!!,
        lowId = row.getLong("low_id")!!,
        highId = row.getLong("high_id")!!,
        lowDeleteMarkedId = row.getLong("low_delete_marked_id"),
        highDeleteMarkedId = row.getLong("high_delete_marked_id"),
        lastLetterEntity = LetterEntity(
            id = row.getLong("l_id")!!,
            letterBoxId = row.getLong("id")!!,
            sentBy = row.getLong("l_sent_by")!!,
            sentTo = row.getLong("l_sent_to")!!,
            text = row.getString("l_text")!!,
            createdAt = row.getLocalDateTime("l_created_at")!!,
            imagePath = row.getString("l_image_path"),
            isRead = row.getBoolean("l_is_read")!!
        )
    )
}
