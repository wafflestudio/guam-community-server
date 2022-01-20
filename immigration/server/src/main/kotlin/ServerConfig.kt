package waffle.guam.immigration.server

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@ComponentScan
@EnableR2dbcRepositories
class ServerConfig
