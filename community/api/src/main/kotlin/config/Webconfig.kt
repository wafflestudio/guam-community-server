package waffle.guam.community.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import waffle.guam.community.controller.auth.AuthService

@Configuration
class WebConfig(
    private val authService: AuthService
) : WebMvcConfigurer {

    @Value("\${spring.profiles.active:dev}")
    private val activeProfile: String = ""

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        when (activeProfile) {
            "test" -> resolvers.add(UserContextResolverForTest(authService))
            else -> resolvers.add(UserContextResolver(authService))
        }
    }
}
