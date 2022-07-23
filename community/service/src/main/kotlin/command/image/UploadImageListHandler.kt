package waffle.guam.community.service.command.image

import org.springframework.stereotype.Service
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.EventResult
import waffle.guam.community.service.domain.image.ImageType
import waffle.guam.community.service.storage.StorageClient

interface UploadImageListHandler : CommandHandler<UploadImageList, ImageListUploaded>
@Service
class UploadImageListHandlerImpl(
    private val storageClient: StorageClient,
) : UploadImageListHandler {
    override fun handle(command: UploadImageList): ImageListUploaded = command.run {
        imagePaths.map { path ->
            val remotePath = "${storageClient.storagePrefix}/$parentId/$type/$path"
            val preSignedUrl = storageClient.getPresignedUrl(remotePath)
            ImagePath(remotePath, preSignedUrl)
        }.let(::ImageListUploaded)
    }
}

data class UploadImageList(
    val parentId: Long,
    val type: ImageType,
    val imagePaths: List<String>,
) : Command

data class ImageListUploaded(
    private val imagePaths: List<ImagePath>,
) : EventResult {
    val dbPaths: List<String>
        get() = imagePaths.map { it.resultPath }

    val preSignedUrls: List<String>
        get() = imagePaths.map { it.presignedUrl }
}

data class ImagePath(
    val resultPath: String,
    val presignedUrl: String,
)
