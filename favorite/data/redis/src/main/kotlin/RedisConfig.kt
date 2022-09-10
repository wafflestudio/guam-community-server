package waffle.guam.favorite.data.redis

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
class RedisConfig {
    companion object {
        const val POST_LIKE_KEY = "POST_LIKE"
        const val POST_SCRAP_KEY = "POST_SCRAP"
        const val COMMENT_LIKE_KEY = "POST_COMMENT_LIKE_KEY"
    }
}
