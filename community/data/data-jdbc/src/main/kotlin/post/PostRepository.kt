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
import waffle.guam.community.data.jdbc.times
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType

interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findAll(spec: Specification<PostEntity>): List<PostEntity>
    fun findAll(spec: Specification<PostEntity>, sort: Sort): List<PostEntity>

    fun count(spec: Specification<PostEntity>): Long
    fun findAll(spec: Specification<PostEntity>, pageable: Pageable): Page<PostEntity>
    fun findOne(spec: Specification<PostEntity>): PostEntity?
}

fun postId(postId: Long): Specification<PostEntity> = eq("id", postId)

fun postIds(postIds: Collection<Long>): Specification<PostEntity> = `in`("id", postIds)

fun beforePostId(postId: Long?): Specification<PostEntity> = lt("id", postId)

fun status(status: PostEntity.Status): Specification<PostEntity> = eq("status", status)

fun statusIn(statuses: List<PostEntity.Status>): Specification<PostEntity> = `in`("status", statuses)

fun boardId(boardId: Long?): Specification<PostEntity> = eq("boardId", boardId)

fun userId(userId: Long?): Specification<PostEntity> = eq("userId", userId)

fun fetchCategories(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
    query.distinct(true)
    root.fetch<PostEntity, PostCategoryEntity>("categories", JoinType.LEFT)
        .also {
            it.fetch<PostCategoryEntity, CategoryEntity>("category", JoinType.LEFT)
        }
    criteriaBuilder.conjunction()
}

fun fetchCategoriesIdMatching(id: Long?): Specification<PostEntity> {
    return fetchCategories() * Specification { _, criteriaQuery, criteriaBuilder ->
        if (id == null) {
            null
        } else {
            criteriaBuilder.equal(
                criteriaQuery.from(PostCategoryEntity::class.java).get<Any>("id"),
                id,
            )
        }
    }
}

fun fetchComments(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
    query.distinct(true)
    val fetchJoin = root.fetch<PostEntity, PostCommentEntity>("comments", JoinType.LEFT) as Join<*, *>
    criteriaBuilder.equal(fetchJoin.get<Any>("status"), PostCommentEntity.Status.VALID)
    criteriaBuilder.conjunction()
}

fun fulltext(keyword: String): Specification<PostEntity> {
    return Specification { root, criteriaQuery, criteriaBuilder ->
        val match = criteriaBuilder.function(
            "match", Double::class.java,
            root.get<PostEntity>("title"),
            root.get<PostEntity>("content"),
            criteriaBuilder.literal(keyword),
        )

        if (keyword.isNotBlank()) {
            criteriaBuilder.greaterThan(match, 0.0)
        } else null
    }
}
