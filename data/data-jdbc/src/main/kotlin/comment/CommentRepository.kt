package waffle.guam.community.data.jdbc.comment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<CommentEntity, Long> {
    fun findAll(spec: Specification<CommentEntity>): List<CommentEntity>
    fun findAll(spec: Specification<CommentEntity>, pageable: Pageable): Page<CommentEntity>
}
