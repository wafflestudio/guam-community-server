package waffle.guam.favorite.batch.job

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class BatchConfig : DefaultBatchConfigurer() {
    override fun setDataSource(dataSource: DataSource) = Unit
}
