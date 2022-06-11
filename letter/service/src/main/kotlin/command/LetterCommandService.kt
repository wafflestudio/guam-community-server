package waffle.guam.favorite.service.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.favorite.service.domain.Letter
import waffle.guam.favorite.service.domain.toDomain
import waffle.guam.letter.data.r2dbc.data.LetterBoxEntity
import waffle.guam.letter.data.r2dbc.data.LetterEntity
import waffle.guam.letter.data.r2dbc.repository.LetterBoxRepository
import waffle.guam.letter.data.r2dbc.repository.LetterRepository

interface LetterCommandService {
    suspend fun createLetter(command: CreateLetter): Letter
    suspend fun emptyLetterBox(command: EmptyLetterBox)
}

@Service
class LetterCommandServiceImpl(
    private val letterBoxRepository: LetterBoxRepository,
    private val letterRepository: LetterRepository,
) : LetterCommandService {

    // TODO: block, image
    @Transactional
    override suspend fun createLetter(command: CreateLetter): Letter {
        val (userId, pairId, text, image) = command

        val letterBox = letterBoxRepository.find(userId = userId, pairId = pairId)
            ?: letterBoxRepository.save(LetterBoxEntity(userId = userId, pairId = pairId))

        return letterRepository.save(
            LetterEntity(
                letterBoxId = letterBox.id,
                sentBy = userId,
                sentTo = pairId,
                text = text,
                imagePath = null,
            )
        ).toDomain()
    }

    @Transactional
    override suspend fun emptyLetterBox(command: EmptyLetterBox) {
        val (userId, pairId) = command

        val letterBox = letterBoxRepository.find(userId = userId, pairId = pairId, letterSize = 1)
            ?: throw RuntimeException()
        val lastLetter = letterBox.letters?.firstOrNull()

        when {
            lastLetter != null && letterBox.lowId == userId -> {
                letterBoxRepository.save(letterBox.copy(lowDeleteMarkedId = lastLetter.id))
            }
            lastLetter != null && letterBox.highId == userId -> {
                letterBoxRepository.save(letterBox.copy(highDeleteMarkedId = lastLetter.id))
            }
        }
    }
}

data class CreateLetter(
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    val image: MultipartFile?,
)

data class EmptyLetterBox(
    val userId: Long,
    val pairId: Long,
)
