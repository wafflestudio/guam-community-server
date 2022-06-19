package waffle.guam.letter.data.r2dbc.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Service
import waffle.guam.letter.data.r2dbc.data.LetterEntity

interface LetterRepository {
    suspend fun save(letter: LetterEntity): LetterEntity
    suspend fun readAll(userId: Long, letterBoxId: Long)
    suspend fun countBySentToAndIsRead(userId: Long, isRead: Boolean): Int
}

@Service
class LetterRepositoryImpl(
    private val letterDao: LetterDao,
    private val dbClient: DatabaseClient,
) : LetterRepository {

    override suspend fun save(letter: LetterEntity): LetterEntity {
        return letterDao.save(letter)
    }

    override suspend fun readAll(userId: Long, letterBoxId: Long) {
        dbClient.sql("UPDATE letters SET is_read = true WHERE letter_box_id = $letterBoxId AND sent_to = $userId")
            .fetch()
            .awaitOneOrNull()
    }

    override suspend fun countBySentToAndIsRead(userId: Long, isRead: Boolean): Int {
        return letterDao.countBySentToAndIsRead(userId, isRead)
    }
}

interface LetterDao : CoroutineCrudRepository<LetterEntity, Long> {
    suspend fun countBySentToAndIsRead(userId: Long, isRead: Boolean): Int
}
