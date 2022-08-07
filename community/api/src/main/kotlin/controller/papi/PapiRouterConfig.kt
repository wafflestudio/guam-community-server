package waffle.guam.community.controller.papi

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PapiRouterConfig(
    private val papiHandler: PapiHandler,
) {
    @Bean
    fun papiRoute(): RouterFunction<ServerResponse> = coRouter {
        "/papi/v1".nest {
            GET("/posts", accept(MediaType.APPLICATION_JSON), papiHandler::getPosts)
            GET("/comments", accept(MediaType.APPLICATION_JSON), papiHandler::getComments)
        }
    }
}
