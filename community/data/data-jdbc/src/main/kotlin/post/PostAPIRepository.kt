package waffle.guam.community.data.jdbc.post

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import waffle.guam.community.data.jdbc.comment.QPostCommentEntity.postCommentEntity
import waffle.guam.community.data.jdbc.post.QPostEntity.postEntity

@Repository
class PostAPIRepository(
    private val querydsl: JPAQueryFactory,
) {
    fun findPostsOfUser(
        userId: Long? = null,
        beforePostId: Long? = null,
        pageSize: Long = 20L,
    ): List<PostEntity> = querydsl
        .select(postEntity)
        .from(postEntity)
        .leftJoin(postEntity.comments, postCommentEntity)
        .where(
            eqUserId(userId),
            ltId(beforePostId),
        )
        .groupBy(postEntity.id)
        .orderBy(postEntity.id.desc())
        .limit(pageSize)
        .fetch()

    private fun eqId(id: Long?) =
        id?.run { postEntity.id.eq(this) }

    private fun ltId(id: Long?) =
        id?.run { postEntity.id.lt(this) }

    private fun eqUserId(userId: Long?) =
        userId?.run { postEntity.userId.eq(this) }

    var sortCriteria: NumberPath<Long> = Expressions.numberPath(Long::class.java, "sortCriteria")
}
