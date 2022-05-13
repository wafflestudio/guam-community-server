package waffle.guam.community.service.command.letter

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.common.GuamBadRequest
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.domain.image.ImageType

@Service
class CreateLetterHandler(
    private val letterRepository: LetterRepository,
    private val uploadImageListHandler: UploadImageListHandler,
) : CommandHandler<CreateLetter, LetterCreated> {
    @Transactional
    override fun handle(command: CreateLetter): LetterCreated {
        val letters = listOf(
            LetterEntity(userId = command.from, sentBy = command.from, sentTo = command.to, text = command.text),
            LetterEntity(userId = command.to, sentBy = command.from, sentTo = command.to, text = command.text),
        ).let(letterRepository::saveAll)

        letters.onEach { it.addImage(command.image) }
        return LetterCreated(from = command.from, to = command.to, text = command.text)
    }

    private fun LetterEntity.addImage(image: MultipartFile?) {
        val imgList = listOf(image ?: return)
        val result = uploadImageListHandler.handle(UploadImageList(parentId = id, type = ImageType.LETTER, images = imgList))
        this.imagePath = result.imagePaths.single()
    }
}

data class CreateLetter(
    val from: UserId,
    val to: UserId,
    val text: String,
    val image: MultipartFile?,
) : Command {
    init {
        require(from != to) { throw GuamBadRequest("자기 자신에게 쪽지를 보낼 수 없습니다.") }
        require(text.isNotBlank()) { throw GuamBadRequest("내용을 입력해주세요.") }
    }
}

data class LetterCreated(
    val from: UserId,
    val to: UserId,
    val text: String,
) : Result
