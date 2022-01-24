package waffle.guam.immigration.app

import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.logging.LogLevel
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.logging.LoggingService
import com.linecorp.armeria.spring.ArmeriaServerConfigurator
import io.grpc.BindableService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.Duration

fun main() {
    runApplication<ImmigrationApplication>()
}

@SpringBootApplication
class ImmigrationApplication {
    @Bean
    fun armeriaConfig(
        grpcServices: List<BindableService>,
    ) = ArmeriaServerConfigurator { builder ->
        builder
            .requestTimeout(Duration.ofSeconds(5))
            .serviceUnder("/docs", DocService.builder().build())
            .service("/") { _, _ -> HttpResponse.of(200) }
            .service(
                GrpcService.builder()
                    .addServices(grpcServices)
                    .enableUnframedRequests(true)
                    .build()
            ).decorator(
                LoggingService.builder()
                    .run {
                        requestLogLevel(LogLevel.DEBUG)
                        successfulResponseLogLevel(LogLevel.INFO)
                        failureResponseLogLevel(LogLevel.ERROR)
                    }
                    .newDecorator()
            )
    }
}
