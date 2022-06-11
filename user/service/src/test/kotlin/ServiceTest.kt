package waffle.guam.user.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    "spring.cloud.vault.enabled=false",
    "spring.config.location=classpath:application-service.yaml"
)
annotation class ServiceTest {
    @SpringBootApplication(exclude = [ServiceConfig::class])
    class ServiceTestApplication
}
