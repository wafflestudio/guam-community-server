package waffle.guam.letter.service.query

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("guam.services.user")
data class UserServiceProperties(
    val baseUrl: String = ""
)
