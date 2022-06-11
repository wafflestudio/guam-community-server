package waffle.guam.letter.data.r2dbc.repository

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import waffle.guam.letter.data.r2dbc.data.LetterBoxEntity
import waffle.guam.letter.data.r2dbc.data.LetterEntity
import waffle.guam.letter.data.r2dbc.data.deleteMarkedId
import waffle.guam.letter.data.r2dbc.getBoolean
import waffle.guam.letter.data.r2dbc.getLocalDateTime
import waffle.guam.letter.data.r2dbc.getLong
import waffle.guam.letter.data.r2dbc.getString
import java.lang.Long.min
import kotlin.math.max

interface LetterBoxRepository {
    suspend fun find(
        userId: Long,
        pairId: Long,
        letterSize: Int? = null,
        letterIdSmallerThan: Long? = null,
    ): LetterBoxEntity?

    suspend fun save(letterBox: LetterBoxEntity): LetterBoxEntity
}

@Service
internal class LetterBoxRepositoryImpl(
    private val dbClient: DatabaseClient,
    private val letterBoxDao: LetterBoxDao,
) : LetterBoxRepository {

    override suspend fun find(
        userId: Long,
        pairId: Long,
        letterSize: Int?,
        letterIdSmallerThan: Long?,
    ): LetterBoxEntity? {
        if (letterSize == 0) {
            // no join with letters
            return findSimple(userId, pairId)
        }

        val letterBox = findSimple(userId, pairId) ?: return null
        val deleteMarkedId = letterBox.deleteMarkedId(userId)

        var where = "WHERE letter_box_id = ${letterBox.id}"
        if (letterIdSmallerThan != null) {
            where += " AND id < $letterIdSmallerThan"
        }
        if (deleteMarkedId != null) {
            where += " AND id > $deleteMarkedId"
        }

        val limit = if (letterSize == null) {
            ""
        } else {
            "LIMIT $letterSize"
        }

        val letters = dbClient.sql("SELECT * FROM letters $where ORDER by id DESC $limit")
            .map { r -> LetterEntity(r) }
            .flow()
            .toList()

        return letterBox.copy(letters = letters)
    }

    private suspend fun findSimple(userId: Long, pairId: Long): LetterBoxEntity? {
        val aId = min(userId, pairId)
        val bId = max(userId, pairId)

        return letterBoxDao.findByLowIdAndHighId(aId, bId)
    }

    override suspend fun save(letterBox: LetterBoxEntity): LetterBoxEntity {
        return letterBoxDao.save(letterBox)
    }
}

private fun LetterEntity(row: Row): LetterEntity = row.run {
    LetterEntity(
        id = getLong("id")!!,
        letterBoxId = getLong("letter_box_id")!!,
        sentBy = getLong("sent_by")!!,
        sentTo = getLong("sent_to")!!,
        text = getString("text"),
        imagePath = getString("image_path"),
        isRead = getBoolean("is_read")!!,
        createdAt = getLocalDateTime("created_at")!!
    )
}

internal interface LetterBoxDao : CoroutineCrudRepository<LetterBoxEntity, Long> {
    suspend fun findByLowIdAndHighId(aId: Long, bId: Long): LetterBoxEntity?
}
