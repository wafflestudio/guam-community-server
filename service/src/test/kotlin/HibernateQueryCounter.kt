package waffle.guam.community

import org.hibernate.EmptyInterceptor
import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.hibernate5.SpringBeanContainer
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.sql.DataSource

interface HibernateQueryCounter {
    fun <K> count(block: () -> K): Result<K>

    data class Result<K>(
        val value: K,
        val queryCount: Int,
    )
}

class HibernateQueryCounterImpl : EmptyInterceptor(), HibernateQueryCounter {
    private val isCounting: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
    private val queryCount: ThreadLocal<Int> = ThreadLocal()

    override fun onPrepareStatement(sql: String?): String {
        if (isCounting.get()) {
            queryCount.set(queryCount.get() + 1)
        }

        return super.onPrepareStatement(sql)
    }

    override fun <K> count(block: () -> K): HibernateQueryCounter.Result<K> {
        isCounting.set(true)
        queryCount.set(0)

        val result = block()

        isCounting.set(false)

        return HibernateQueryCounter.Result(result, queryCount.get())
    }
}

@Configuration
class HibernateConfig {
    @Bean
    fun entityManagerFactory(
        factory: EntityManagerFactoryBuilder,
        dataSource: DataSource,
        jpaProperties: JpaProperties,
        hibernateProperties: HibernateProperties,
        beanFactory: ConfigurableListableBeanFactory,
        customInterceptor: HibernateQueryCounterImpl,
    ): LocalContainerEntityManagerFactoryBean? {
        val properties: Map<String, Any> =
            hibernateProperties.determineHibernateProperties(
                jpaProperties.properties,
                HibernateSettings().ddlAuto { "create-drop" }
            ).also { it.put("hibernate.ejb.interceptor", customInterceptor) }

        return factory.dataSource(dataSource)
            .packages("waffle.guam.community")
            .properties(properties)
            .build()
            .also { it.jpaPropertyMap.put(AvailableSettings.BEAN_CONTAINER, SpringBeanContainer(beanFactory)) }
    }

    @Bean
    fun customInterceptor() = HibernateQueryCounterImpl()
}
