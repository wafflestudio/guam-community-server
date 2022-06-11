package waffle.guam.community

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(HibernateConfig::class)
@SpringBootApplication
class TestApplication
