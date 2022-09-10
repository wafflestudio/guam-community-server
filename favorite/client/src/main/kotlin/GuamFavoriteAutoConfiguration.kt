package waffle.guam.favorite.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import waffle.guam.favorite.client.impl.GuamFavoriteAsyncClientImpl
import waffle.guam.favorite.client.impl.GuamFavoriteClientImpl

@EnableConfigurationProperties(GuamFavoriteProperties::class)
@Configuration
class GuamFavoriteAutoConfiguration {

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Configuration
    internal inner class MvcConfiguration {
        @ConditionalOnMissingBean(GuamFavoriteClient::class)
        @Bean
        fun blockingClient(
            properties: GuamFavoriteProperties,
            env: Environment,
        ): GuamFavoriteClient =
            if (properties.url != null) {
                GuamFavoriteClientImpl(properties.url)
            } else {
                GuamFavoriteClientImpl(url(env))
            }
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Configuration
    internal inner class WebfluxConfiguration {
        @ConditionalOnMissingBean(GuamFavoriteClient.Async::class)
        @Bean
        fun client(
            properties: GuamFavoriteProperties,
            env: Environment,
        ): GuamFavoriteClient.Async =
            if (properties.url != null) {
                GuamFavoriteAsyncClientImpl(properties.url)
            } else {
                GuamFavoriteAsyncClientImpl(url(env))
            }
    }

    private fun url(env: Environment) = when {
        env.acceptsProfiles(Profiles.of("local")) -> "http://guam-favorite.jon-snow-korea.com"
        env.acceptsProfiles(Profiles.of("dev")) -> "http://guam-favorite.jon-snow-korea.com"
        else -> TODO()
    }
}
