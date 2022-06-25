package waffle.guam.community.data.jdbc.comment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository

interface PostCommentRepository : JpaRepository<PostCommentEntity, Long> {
    fun findAll(spec: Specification<PostCommentEntity>): List<PostCommentEntity>
    fun findAllByUserIdAndIdLessThanAndStatusOrderByIdDesc(userId: Long, beforeId: Long, status: PostCommentEntity.Status, pageable: Pageable): Page<PostCommentEntity>
}
