package waffle.guam.community.data.jdbc.post

import org.springframework.data.jpa.domain.Specification
import waffle.guam.community.data.jdbc.QueryGenerator
import waffle.guam.community.data.jdbc.category.PostCategoryEntity_
import javax.persistence.criteria.JoinType

interface PostQueryGenerator : QueryGenerator<PostEntity> {
    fun postId(postId: Long): Specification<PostEntity> = eq(PostEntity_.ID, postId)

    fun postIds(postIds: Collection<Long>): Specification<PostEntity> = `in`(PostEntity_.ID, postIds)

    fun beforePostId(postId: Long): Specification<PostEntity> = lt(PostEntity_.ID, postId)

    fun status(status: PostEntity.Status): Specification<PostEntity> = eq(PostEntity_.STATUS, status)

    fun statusIn(statuses: List<PostEntity.Status>): Specification<PostEntity> = `in`(PostEntity_.STATUS, statuses)

    fun boardId(boardId: Long?): Specification<PostEntity> = eq(PostEntity_.BOARD_ID, boardId)

    fun fetchCategories(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch(PostEntity_.categories, JoinType.LEFT).also {
            it.fetch(PostCategoryEntity_.category, JoinType.LEFT)
        }
        criteriaBuilder.conjunction()
    }

    fun fetchComments(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch(PostEntity_.comments, JoinType.LEFT)
        criteriaBuilder.conjunction()
    }

    fun Collection<PostEntity>.throwIfNotContainIds(postIds: Collection<Long>) = apply {
        val missed = postIds - map { it.id }.toSet()

        if (missed.isNotEmpty()) {
            throw RuntimeException()
        }
    }
}
