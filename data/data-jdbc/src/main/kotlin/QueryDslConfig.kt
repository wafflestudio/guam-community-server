package waffle.guam.community.data.jdbc

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class QueryDslConfig(
    @PersistenceContext
    private val entityManager: EntityManager,
) {
    @Bean
    fun jpaQueryFactory(): JPAQueryFactory =
        JPAQueryFactory(entityManager)
}
