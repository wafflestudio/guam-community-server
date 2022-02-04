package waffle.guam.community.service.command.letter

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.domain.image.ImageType

@Service
class CreateLetterHandler(
    private val letterRepository: LetterRepository,
    private val imageHandler: UploadImageListHandler,
) : CommandHandler<CreateLetter, LetterCreated> {

    @Transactional
    override fun handle(command: CreateLetter): LetterCreated {
        val letter = letterRepository.save(LetterEntity(command.from, command.to, command.text))
        val imagePath = letter.addImage(command.image)

        return LetterCreated(command.from, command.to, command.text, imagePath)
    }

    private fun LetterEntity.addImage(image: MultipartFile?): String? = image?.let {
        imageHandler
            .handle(UploadImageList(id, ImageType.LETTER, listOf(image)))
            .imagePaths.single()
    }.also { imagePath ->
        this.image = imagePath
    }
}

data class CreateLetter(
    val from: Long,
    val to: Long,
    val text: String,
    val image: MultipartFile?,
) : Command

data class LetterCreated(
    val from: Long,
    val to: Long,
    val text: String,
    val imagePath: String?,
) : Result
