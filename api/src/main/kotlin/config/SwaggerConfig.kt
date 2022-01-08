package waffle.guam.community.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.parameters.Parameter
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
            .group("guam")
            .pathsToMatch("/**")
            .addOpenApiCustomiser { openApi ->
                // auth 관련 API 빼고 Operation (API)에 인증 헤더를 추가해서 보여준다.
                // https://github.com/springdoc/springdoc-openapi/issues/708
                openApi.paths
                    .filterNot { (pathName, _) -> pathName.contains("/auth") }
                    .flatMap { (_, pathItem) -> pathItem.readOperations() }
                    .forEach { operation ->
                        operation.addParametersItem(
                            Parameter()
                                .`in`("header")
                                .name("Authorization")
                                .required(true)
                        )
                    }
            }
            .build()
    }
}
