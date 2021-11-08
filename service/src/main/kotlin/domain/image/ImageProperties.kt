package waffle.guam.community.service.domain.image

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("image")
data class ImageProperties(
    val storage: ImageStorage = ImageStorage.LOCAL,
)

enum class ImageStorage {
    LOCAL, DEV, PROD
}

enum class ImageType {
    POST, COMMENT
}
