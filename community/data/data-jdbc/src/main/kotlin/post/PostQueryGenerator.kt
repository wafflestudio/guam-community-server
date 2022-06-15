package waffle.guam.community.data.jdbc.post

import org.springframework.data.jpa.domain.Specification
import waffle.guam.community.data.jdbc.QueryGenerator
import waffle.guam.community.data.jdbc.category.CategoryEntity
import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import javax.persistence.criteria.JoinType

interface PostQueryGenerator : QueryGenerator<PostEntity> {
    fun postId(postId: Long): Specification<PostEntity> = eq("id", postId)

    fun postIds(postIds: Collection<Long>): Specification<PostEntity> = `in`("id", postIds)

    fun beforePostId(postId: Long): Specification<PostEntity> = lt("id", postId)

    fun status(status: PostEntity.Status): Specification<PostEntity> = eq("status", status)

    fun statusIn(statuses: List<PostEntity.Status>): Specification<PostEntity> = `in`("status", statuses)

    fun boardId(boardId: Long?): Specification<PostEntity> = eq("boardId", boardId)

    fun fetchCategories(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch<PostEntity, PostCategoryEntity>("categories", JoinType.LEFT)
            .also {
                it.fetch<PostCategoryEntity, CategoryEntity>("category", JoinType.LEFT)
            }
        criteriaBuilder.conjunction()
    }

    fun fetchComments(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch<PostEntity, PostCommentEntity>("comments", JoinType.LEFT)
        criteriaBuilder.conjunction()
    }

    fun Collection<PostEntity>.throwIfNotContainIds(postIds: Collection<Long>) = apply {
        val missed = postIds - map { it.id }.toSet()

        if (missed.isNotEmpty()) {
            throw RuntimeException()
        }
    }
}
