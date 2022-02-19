package waffle.guam.community.data.jdbc.post

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findAll(spec: Specification<PostEntity>): List<PostEntity>
    fun findAll(spec: Specification<PostEntity>, pageable: Pageable): Page<PostEntity>
    fun findOne(spec: Specification<PostEntity>): PostEntity?
}
