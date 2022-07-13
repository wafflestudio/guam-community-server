package waffle.guam.community.service.storage

import waffle.guam.community.service.domain.image.ImageStorage
import java.io.File

interface StorageClient {
    val storagePrefix: ImageStorage
    fun upload(objectPath: String, file: File)
    fun upload(files: List<File>)

    fun getPresignedUrl(path: String): String
}
