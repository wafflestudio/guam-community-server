package waffle.guam.community.data.jdbc.post

import org.springframework.data.jpa.domain.Specification
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.jdbc.QueryGenerator
import waffle.guam.community.data.jdbc.comment.PostCommentEntity_
import waffle.guam.community.data.jdbc.tag.PostTagEntity_
import javax.persistence.criteria.JoinType

interface PostQueryGenerator : QueryGenerator<PostEntity> {
    fun postId(postId: Long): Specification<PostEntity> = eq(PostEntity_.ID, postId)

    fun postIds(postIds: Collection<Long>): Specification<PostEntity> = `in`(PostEntity_.ID, postIds)

    fun beforePostId(postId: Long): Specification<PostEntity> = lt(PostEntity_.ID, postId)

    fun status(status: PostEntity.Status): Specification<PostEntity> = eq(PostEntity_.STATUS, status)

    fun statusIn(statuses: List<PostEntity.Status>): Specification<PostEntity> = `in`(PostEntity_.STATUS, statuses)

    fun boardId(boardId: Long?): Specification<PostEntity> = eq(PostEntity_.BOARD_ID, boardId)

    fun fetchTags(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch(PostEntity_.tags, JoinType.LEFT).also {
            it.fetch(PostTagEntity_.tag, JoinType.LEFT)
        }
        criteriaBuilder.conjunction()
    }

    fun fetchComments(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch(PostEntity_.comments, JoinType.LEFT).also {
            it.fetch(PostCommentEntity_.user, JoinType.LEFT)
        }
        criteriaBuilder.conjunction()
    }

    fun fetchLikes(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch(PostEntity_.likes, JoinType.LEFT)
        criteriaBuilder.conjunction()
    }

    fun fetchScraps(): Specification<PostEntity> = Specification { root, query, criteriaBuilder ->
        query.distinct(true)
        root.fetch(PostEntity_.scraps, JoinType.LEFT)
        criteriaBuilder.conjunction()
    }

    fun Collection<PostEntity>.throwIfNotContainIds(postIds: Collection<Long>) = apply {
        val missed = postIds - map { it.id }

        if (missed.isNotEmpty()) {
            throw PostNotFound(missed)
        }
    }
}
