package waffle.guam.community.service.command.image

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.domain.image.ImageProperties
import waffle.guam.community.service.domain.image.ImageStorage
import waffle.guam.community.service.domain.image.ImageType
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@EnableConfigurationProperties(ImageProperties::class)
@Service
class UploadImageListHandler(
    private val properties: ImageProperties,
) : CommandHandler<UploadImageList, ImageListUploaded> {
    override fun handle(command: UploadImageList): ImageListUploaded {
        val dirPath: Path = Paths.get("${properties.storage}/${command.type}/${command.parentId}")
            .also { Files.createDirectories(it) }

        val files = command.images.mapIndexed { i, it ->
            it.inputStream.use { inputStream ->
                val filename = "$i.${getExtension(it.originalFilename)}"

                dirPath.resolve(filename).let {
                    Files.copy(inputStream, it, StandardCopyOption.REPLACE_EXISTING)
                    it.toFile()
                }
            }
        }

        when (properties.storage) {
            ImageStorage.LOCAL -> {
                // DO NOTHING
            }
            else -> {
                TODO("upload to s3 and delete image files")
            }
        }

        return files.map { it.path }
            .let(::ImageListUploaded)
    }

    // TODO: format 체크 제대로
    private fun getExtension(filename: String): String? =
        if (filename.contains(".")) {
            filename.substring(filename.lastIndexOf(".") + 1)
        } else {
            null
        }
}

data class UploadImageList(
    val parentId: Long,
    val type: ImageType,
    val images: List<MultipartFile>,
) : Command

data class ImageListUploaded(
    val imagePaths: List<String>,
) : Result
