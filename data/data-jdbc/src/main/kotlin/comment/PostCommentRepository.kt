package waffle.guam.community.data.jdbc.comment

import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository

interface PostCommentRepository : JpaRepository<PostCommentEntity, Long> {
    fun findAll(spec: Specification<PostCommentEntity>): List<PostCommentEntity>
    fun findOne(spec: Specification<PostCommentEntity>): PostCommentEntity?
}
