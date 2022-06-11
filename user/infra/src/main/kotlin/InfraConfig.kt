package waffle.guam.user.infra

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["waffle.guam.user"])
@EnableJpaRepositories(basePackages = ["waffle.guam.user"])
@Configuration
@ComponentScan
class InfraConfig
