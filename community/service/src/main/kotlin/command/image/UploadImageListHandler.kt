package waffle.guam.community.service.command.image

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.domain.image.ImageType
import waffle.guam.community.service.storage.StorageClient
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class UploadImageListHandler(
    private val storageClient: StorageClient,
) : CommandHandler<UploadImageList, ImageListUploaded> {
    override fun handle(command: UploadImageList): ImageListUploaded {
        val dirPath: Path = Paths
            .get("${storageClient.storagePrefix}/${command.type}/${command.parentId}")
            .apply(Files::createDirectories)

        val files: List<File> = command.images.copyTo(dirPath)

        storageClient.upload(files)

        return files.map { it.path }.let(::ImageListUploaded)
    }

    private fun List<MultipartFile>.copyTo(directory: Path) = filterNot { it.isEmpty }.mapIndexed { idx, file ->
        val storageFileName = "$idx.${file.originalFilename?.substringAfterLast(".", "")}"
        val copiedPath = directory.resolve(storageFileName)
        file.inputStream.use { inputStream ->
            Files.copy(inputStream, copiedPath, StandardCopyOption.REPLACE_EXISTING)
            copiedPath.toFile()
        }
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
