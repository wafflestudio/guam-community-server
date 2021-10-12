package waffle.guam.community.service.query

import org.springframework.data.jpa.domain.Specification

interface Query<E> {
    val spec: Specification<E>
}
