package waffle.guam.community.service.query

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface Collector<Q, D> {
    fun get(query: Q): D
    fun gets(query: Q): List<D>
    fun gets(query: Q, pageable: Pageable): Page<D>
}
