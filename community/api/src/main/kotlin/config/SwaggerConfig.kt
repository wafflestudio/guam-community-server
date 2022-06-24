package waffle.guam.community.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.parameters.Parameter
import org.springdoc.core.GroupedOpenApi
import org.springdoc.core.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import waffle.guam.community.UserContext

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
            .group("Guam")
            .addOperationCustomizer { operation, _ ->
                val headerParameter = Parameter()
                    .`in`("header")
                    .required(false)
                    .name(GATEWAY_HEADER_NAME)

                operation.apply {
                    if (parameters?.contains(headerParameter) != true) {
                        addParametersItem(headerParameter)
                    }
                }
            }
            .build()
    }
}
