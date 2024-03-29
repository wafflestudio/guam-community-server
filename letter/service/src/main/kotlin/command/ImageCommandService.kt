package waffle.guam.letter.service.command

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.time.Duration

interface ImageCommandService {
    suspend fun upload(letterBoxId: Long, images: List<FilePart>): List<String>
}

@EnableConfigurationProperties(S3Properties::class)
@Service
class ImageCommandServiceImpl(
    private val s3Properties: S3Properties,
    env: Environment,
) : ImageCommandService {

    private val s3Client: S3AsyncClient by lazy { buildClient() }
    private val prefix = when {
        env.acceptsProfiles(Profiles.of("dev")) -> "DEV"
        env.acceptsProfiles(Profiles.of("prod")) -> "PROD"
        else -> "LOCAL"
    }

    override suspend fun upload(letterBoxId: Long, images: List<FilePart>): List<String> {
        require(images.isNotEmpty())

        return images.map { upload(letterBoxId = letterBoxId, image = it) }
    }

    private suspend fun upload(letterBoxId: Long, image: FilePart): String {
        val path = resolvePath(letterBoxId = letterBoxId, image = image)
        val file = File("temp_$letterBoxId").also {
            image.transferTo(it).awaitSingleOrNull()
            it.deleteOnExit()
        }

        Mono.fromFuture(
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(s3Properties.bucket)
                    .contentLength(file.length())
                    .contentType(image.headers().contentType!!.type)
                    .key(path)
                    .acl("public-read")
                    .build(),
                AsyncRequestBody.fromFile(file)
            )
        ).awaitSingle()

        return path.toString()
    }

    private fun resolvePath(letterBoxId: Long, image: FilePart): String {
        val uid = System.currentTimeMillis() // TODO: UUID로 하고싶은데 blocking call..
        val extension = image.filename().split(".")[1]

        return "$prefix/LETTER/$letterBoxId/$uid.$extension"
    }

    // https://www.baeldung.com/java-aws-s3-reactive
    private fun buildClient(): S3AsyncClient {
        val httpClient: SdkAsyncHttpClient = NettyNioAsyncHttpClient.builder()
            .writeTimeout(Duration.ofSeconds(5))
            .maxConcurrency(64)
            .build()
        val serviceConfiguration = S3Configuration.builder()
            .checksumValidationEnabled(false)
            .chunkedEncodingEnabled(true)
            .build()
        val b = S3AsyncClient.builder().httpClient(httpClient)
            .region(Region.of(s3Properties.region))
            .credentialsProvider(StaticCredentialsProvider.create(s3Properties.credentials))
            .serviceConfiguration(serviceConfiguration)

        return b.build()
    }
}

@ConstructorBinding
@ConfigurationProperties("letter.aws.s3")
data class S3Properties(
    val accessKey: String = "",
    val secretKey: String = "",
    val bucket: String = "",
    val region: String = "",
) {
    val credentials: AwsCredentials
        get() = AwsBasicCredentials.create(accessKey, secretKey)
}
