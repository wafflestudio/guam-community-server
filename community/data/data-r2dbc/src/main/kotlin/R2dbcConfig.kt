package waffle.guam.community.data.r2dbc

import com.querydsl.sql.SQLTemplates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class R2dbcConfig {
    @Bean
    fun sqlTemplate(): SQLTemplates {
        return SQLTemplates.DEFAULT
    }
}
