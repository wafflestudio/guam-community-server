package waffle.guam.community.data.jdbc.post

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import waffle.guam.community.data.jdbc.`in`
import waffle.guam.community.data.jdbc.category.CategoryEntity
import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.eq
import waffle.guam.community.data.jdbc.lt
import javax.persistence.criteria.JoinType

interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findAll(spec: Specification<PostEntity>): List<PostEntity>
    fun findAll(spec: Specification<PostEntity>, sort: Sort): List<PostEntity>
    fun findAll(spec: Specification<PostEntity>, pageable: Pageable): Page<PostEntity>
    fun findOne(spec: Specification<PostEntity>): PostEntity?
}

fun postId(postId: Long): Specification<PostEntity> = eq("id", postId)

fun postIds(postIds: Collection<Long>): Specification<PostEntity> = `in`("id", postIds)

fun beforePostId(postId: Long?): Specification<PostEntity> = lt("id", postId)

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
    // fixme VALID comment 만 가져오기
    root.fetch<PostEntity, PostCommentEntity>("comments", JoinType.LEFT)
    criteriaBuilder.conjunction()
}

fun Collection<PostEntity>.throwIfNotContainIds(postIds: Collection<Long>) = apply {
    val missed = postIds - map { it.id }.toSet()

    if (missed.isNotEmpty()) {
        throw RuntimeException()
    }
}
