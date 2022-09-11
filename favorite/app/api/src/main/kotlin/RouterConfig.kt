package waffle.guam.favorite.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Flux
import waffle.guam.favorite.api.handler.CommentLikeApiHandler
import waffle.guam.favorite.api.handler.LikeApiHandler
import waffle.guam.favorite.api.handler.ScrapApiHandler
import waffle.guam.favorite.api.handler.ViewApiHandler
import waffle.guam.favorite.service.ServiceError
import java.net.URI

@Configuration
@OpenAPIDefinition(info = Info(title = "guam-favorite API", version = "v1"))
class RouterConfig(
    private val like: LikeApiHandler,
    private val scrap: ScrapApiHandler,
    private val commentLike: CommentLikeApiHandler,
    private val view: ViewApiHandler,
) {
    private val mapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

    @Bean
    fun indexRouter() = coRouter {
        GET("/") { temporaryRedirect(URI("/swagger-ui.html")).buildAndAwait() }
    }

    @Bean
    fun router() = coRouter {
        // like
        POST("/api/v1/likes/posts/{postId}", like::create)
        DELETE("/api/v1/likes/posts/{postId}", like::delete)

        // scrap
        POST("/api/v1/scraps/posts/{postId}", scrap::create)
        DELETE("/api/v1/scraps/posts/{postId}", scrap::delete)
        GET("/api/v1/scraps/users", scrap::getUsers)

        // comment like
        POST("/api/v1/likes/comments/{postCommentId}", commentLike::create)
        DELETE("/api/v1/likes/comments/{postCommentId}", commentLike::delete)
        GET("/api/v1/likes/comments/count", commentLike::gets)

        // views
        GET("/api/v1/views", view::get)
        GET("/api/v1/views/rank", view::getRank)
    }

    @Bean
    fun errorHandler(): WebFilter = WebFilter { exchange, chain ->
        chain.filter(exchange)
            .onErrorResume {
                if (it is ServiceError) {
                    exchange.response.rawStatusCode = it.status
                    exchange.response.writeWith(
                        Flux.just(
                            exchange.response
                                .bufferFactory()
                                .wrap(mapper.writeValueAsBytes(it.msg))
                        )
                    )
                } else {
                    exchange.response.rawStatusCode = 500
                    exchange.response.setComplete()
                }
            }
    }
}
