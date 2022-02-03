package waffle.guam.community

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

fun main() {
    runApplication<GuamApplication>()
}

@OpenAPIDefinition(servers = [Server(url = "/", description = "Default Server URL")])
@EnableRetry
@SpringBootApplication
class GuamApplication
