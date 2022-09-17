package waffle.guam.letter.service.command

import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.letter.data.r2dbc.data.LetterEntity
import waffle.guam.letter.data.r2dbc.data.clear
import waffle.guam.letter.data.r2dbc.repository.LetterBoxRepository
import waffle.guam.letter.data.r2dbc.repository.LetterRepository
import waffle.guam.letter.service.domain.Letter
import waffle.guam.letter.service.domain.toDomain

interface LetterCommandService {
    suspend fun createLetter(command: CreateLetter): Letter
    suspend fun clearLetterBox(command: ClearLetterBox)
    suspend fun readLetterBox(command: ReadLetterBox)
}

@Service
class LetterCommandServiceImpl(
    private val letterBoxRepository: LetterBoxRepository,
    private val letterRepository: LetterRepository,
    private val imageCommandService: ImageCommandService,
) : LetterCommandService {

    // TODO: block
    // TODO: 실패시 이미지 삭제
    @Transactional
    override suspend fun createLetter(command: CreateLetter): Letter {
        val (userId, pairId, text, images) = command

        val letterBox = letterBoxRepository.findOrSave(userId = userId, pairId = pairId)
        val imagePaths = images?.let { imageCommandService.upload(letterBoxId = letterBox.id, images = it) }

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
    override suspend fun clearLetterBox(command: ClearLetterBox) {
        val (userId, pairId) = command

        val letterBox = letterBoxRepository.find(userId = userId, pairId = pairId, size = 1)
            ?: throw RuntimeException()

        letterRepository.readAll(userId = userId, letterBoxId = letterBox.id)
        letterBoxRepository.save(letterBox.clear(userId))
    }

    @Transactional
    override suspend fun readLetterBox(command: ReadLetterBox) {
        val (userId, pairId) = command

        val letterBox = letterBoxRepository.find(userId = userId, pairId = pairId)
            ?: throw RuntimeException()

        letterRepository.readAll(userId = userId, letterBoxId = letterBox.id)
    }
}

data class CreateLetter(
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    val images: List<FilePart>?,
)

data class ClearLetterBox(
    val userId: Long,
    val pairId: Long,
)

data class ReadLetterBox(
    val userId: Long,
    val pairId: Long,
)
