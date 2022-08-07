package waffle.guam.community.data.r2dbc

import com.google.common.base.CaseFormat
import com.infobip.spring.data.jdbc.annotation.processor.ProjectColumnCaseFormat
import com.querydsl.sql.MySQLTemplates
import com.querydsl.sql.SQLTemplates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty

@Configuration
@ProjectColumnCaseFormat(CaseFormat.LOWER_UNDERSCORE)
class R2dbcConfig {
    @Bean
    fun sqlTemplate(): SQLTemplates {
        return MySQLTemplates.DEFAULT
    }

    @Bean
    @Primary
    fun namingStrategy(): NamingStrategy = object : NamingStrategy {
        override fun getTableName(type: Class<*>): String = type.simpleName
    }
}
