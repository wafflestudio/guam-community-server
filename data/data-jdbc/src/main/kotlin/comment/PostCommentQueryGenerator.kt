package waffle.guam.community.data.jdbc.comment

import org.springframework.data.jpa.domain.Specification
import waffle.guam.community.common.PostCommentNotFound
import waffle.guam.community.data.jdbc.QueryGenerator
import javax.persistence.criteria.JoinType

interface PostCommentQueryGenerator : QueryGenerator<PostCommentEntity> {
    fun commentId(commentId: Long): Specification<PostCommentEntity> = eq(PostCommentEntity_.ID, commentId)

    fun commentIdIn(commentIds: Collection<Long>): Specification<PostCommentEntity> = `in`(PostCommentEntity_.ID, commentIds)

    fun fetchCommentLikes(): Specification<PostCommentEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch(PostCommentEntity_.likes, JoinType.LEFT)
        criteriaBuilder.conjunction()
    }

    fun Collection<PostCommentEntity>.throwIfNotContainIds(commentIds: Collection<Long>) = apply {
        val missed = commentIds - map { it.id }

        if (missed.isNotEmpty()) {
            throw PostCommentNotFound()
        }
    }
}
