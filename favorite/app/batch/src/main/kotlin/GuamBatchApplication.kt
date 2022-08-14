package waffle.guam.favorite.batch

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(DataSourceAutoConfiguration::class)
class GuamBatchApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder(GuamBatchApplication::class.java)
        .web(WebApplicationType.NONE)
        .run(*args)
        .close()
}
