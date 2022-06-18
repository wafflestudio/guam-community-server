package waffle.guam.favorite.service.command

import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.service.domain.Letter
import waffle.guam.favorite.service.domain.LetterBox
import waffle.guam.favorite.service.domain.setReadBy
import waffle.guam.favorite.service.domain.toDomain
import waffle.guam.letter.data.r2dbc.data.LetterBoxEntity
import waffle.guam.letter.data.r2dbc.data.LetterEntity
import waffle.guam.letter.data.r2dbc.repository.LetterBoxRepository
import waffle.guam.letter.data.r2dbc.repository.LetterRepository

interface LetterCommandService {
    suspend fun createLetter(command: CreateLetter): Letter
    suspend fun emptyLetterBox(command: EmptyLetterBox)
    suspend fun readLetterBox(command: ReadLetterBox): LetterBox
}

@Service
class LetterCommandServiceImpl(
    private val letterBoxRepository: LetterBoxRepository,
    private val letterRepository: LetterRepository,
    private val imageCommandService: ImageCommandService,
) : LetterCommandService {

    // TODO: block, image
    @Transactional
    override suspend fun createLetter(command: CreateLetter): Letter {
        val (userId, pairId, text, images) = command

        val letterBox = letterBoxRepository.find(userId = userId, pairId = pairId)
            ?: letterBoxRepository.save(LetterBoxEntity(userId = userId, pairId = pairId))

        val imagePaths = if (images != null && images.isNotEmpty()) {
            images.map { imageCommandService.upload(letterBoxId = letterBox.id, image = it) }
        } else {
            null
        }

        val letter = letterRepository.save(
            LetterEntity(
                letterBoxId = letterBox.id,
                sentBy = userId,
                sentTo = pairId,
                text = text,
                imagePath = imagePaths?.joinToString(","),
            )
        )

        return letter.toDomain()
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

    @Transactional
    override suspend fun readLetterBox(command: ReadLetterBox): LetterBox {
        val (userId, letterBox) = command

        if (userId != letterBox.userId) {
            throw RuntimeException()
        }

        letterRepository.readAll(userId = userId, letterBoxId = letterBox.id)

        return letterBox.copy(
            letters = letterBox.letters.map { it.setReadBy(userId) }
        )
    }
}

data class CreateLetter(
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    val images: List<FilePart>?,
)

data class EmptyLetterBox(
    val userId: Long,
    val pairId: Long,
)

data class ReadLetterBox(
    val userId: Long,
    val letterBox: LetterBox,
)
