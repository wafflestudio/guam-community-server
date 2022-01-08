package waffle.guam.community.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.GroupedOpenApi
import org.springdoc.core.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import waffle.guam.community.common.UserContext

@OpenAPIDefinition(
    info = Info(
        title = "GCS API",
        description = "Guam Community Server API"
    )
)
@Configuration
class SwaggerConfig {

    @Bean
    fun api(): GroupedOpenApi {
        SpringDocUtils
            .getConfig()
            .addRequestWrapperToIgnore(UserContext::class.java) // Hide UserContext

        return GroupedOpenApi
            .builder()
            .group("GUAM API")
            .addOpenApiCustomiser { openApi ->
                openApi
                    .addSecurityItem(SecurityRequirement().addList("FCM Token"))
                    .components.addSecuritySchemes(
                        "FCM Token",
                        SecurityScheme()
                            .name("Authorization")
                            .type(SecurityScheme.Type.HTTP)
                            .`in`(SecurityScheme.In.HEADER)
                            .bearerFormat("JWT")
                            .scheme("bearer")
                    )
            }
            .build()
    }
}
