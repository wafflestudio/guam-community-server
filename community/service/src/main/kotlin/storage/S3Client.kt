package waffle.guam.community.service.storage

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import waffle.guam.community.service.domain.image.ImageStorage
import java.io.File
import java.sql.Date
import java.time.Instant

@Component
@EnableConfigurationProperties(CloudS3Properties::class)
class S3Client(
    env: Environment,
    cloudS3Properties: CloudS3Properties,
    private val s3Client: AmazonS3Client,
) : StorageClient {
    private val bucketName: String = cloudS3Properties.bucket

    override val storagePrefix: ImageStorage =
        env.activeProfiles.mapNotNull { ImageStorage.from(it) }.firstOrNull() ?: ImageStorage.LOCAL

    override fun upload(objectPath: String, file: File) {
        val request = PutObjectRequest(bucketName, objectPath, file)
        s3Client.putObject(request.withCannedAcl(CannedAccessControlList.PublicRead))
    }

    override fun upload(files: List<File>) =
        files.forEach { this.upload(it.path, it) }

    override fun getPresignedUrl(path: String): String {
        val expirationDate = Date.from(Instant.now().plusSeconds(60))
        val request = GeneratePresignedUrlRequest(bucketName, path, HttpMethod.PUT)
            .withExpiration(expirationDate)
        return s3Client.generatePresignedUrl(request).toExternalForm()
    }
}

@ConstructorBinding
@ConfigurationProperties("cloud.aws.s3")
data class CloudS3Properties(
    val bucket: String = "guam"
)
