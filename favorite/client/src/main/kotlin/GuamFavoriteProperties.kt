package waffle.guam.favorite.client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("guam.favorite")
@ConstructorBinding
data class GuamFavoriteProperties(
    val url: String? = null
)
