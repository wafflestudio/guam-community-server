package waffle.guam.community.data.jdbc

import org.springframework.data.jpa.domain.Specification

fun <E> eq(refName: String, targetValue: Any?): Specification<E> {
    return Specification { root, _, criteriaBuilder ->
        if (targetValue != null) {
            criteriaBuilder.equal(root.get<Any>(refName), targetValue)
        } else null
    }
}

fun <E> `in`(refName: String, targetValue: Collection<Any>?): Specification<E> {
    return Specification { root, _, _ ->
        if (!targetValue.isNullOrEmpty()) {
            root.get<Any>(refName).`in`(targetValue)
        } else null
    }
}
