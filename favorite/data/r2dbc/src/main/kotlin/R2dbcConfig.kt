package waffle.guam.favorite.data.r2dbc

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@EnableR2dbcRepositories
@Configuration
@ComponentScan
class R2dbcConfig
