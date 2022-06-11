package waffle.guam.user.infra.aws

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import waffle.guam.user.infra.aws.ImageClient.PathResolver
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import kotlin.io.path.createDirectories

interface ImageClient {
    fun upload(userId: Long, image: MultipartFile): ProfileImage

    interface PathResolver {
        fun resolve(userId: Long, image: MultipartFile): Path
    }
}

@EnableConfigurationProperties(CloudS3Properties::class)
@Service
class ImageClientS3Impl(
    private val s3Client: AmazonS3Client,
    private val property: CloudS3Properties,
    private val pathResolver: PathResolver,
) : ImageClient {

    override fun upload(userId: Long, image: MultipartFile): ProfileImage {
        val filePath: Path = pathResolver.resolve(userId, image)
        val file = image.inputStream
            .use { Files.copy(it, filePath, REPLACE_EXISTING) }
            .let { filePath.toFile() }

        s3Client.putObject(
            PutObjectRequest(property.bucket, file.path, file).withCannedAcl(PublicRead)
        )

        file.parentFile.deleteRecursively()

        return ProfileImage(file.path)
    }
}

@ConstructorBinding
@ConfigurationProperties("cloud.s3")
data class CloudS3Properties(
    val bucket: String = "guam",
)

@Service
class PathResolverImpl(
    private val env: Environment,
) : PathResolver {

    override fun resolve(userId: Long, image: MultipartFile): Path {
        val prefix = when {
            env.acceptsProfiles(Profiles.of("dev")) -> "DEV"
            env.acceptsProfiles(Profiles.of("prod")) -> "PROD"
            else -> "LOCAL"
        }
        val filename = image.originalFilename.let(::requireNotNull)

        return Paths.get("$prefix/PROFILE/$userId")
            .let { it.createDirectories() }
            .resolve(filename)
    }
}
