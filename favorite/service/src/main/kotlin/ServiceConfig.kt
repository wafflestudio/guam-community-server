package waffle.guam.favorite.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(ServiceProperties::class)
@Configuration
@ComponentScan
class ServiceConfig

@ConfigurationProperties("favorite.infra")
@ConstructorBinding
data class ServiceProperties(
    val community: Community,
    val kafka: Kafka,
) {
    data class Community(
        val url: String,
    )

    data class Kafka(
        val url: String,
        val clientId: String = "guam-favorite-dev",
    )
}
