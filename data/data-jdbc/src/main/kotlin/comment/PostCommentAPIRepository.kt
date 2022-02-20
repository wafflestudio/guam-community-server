package waffle.guam.community.data.jdbc.comment

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import waffle.guam.community.data.jdbc.comment.projection.MyCommentView
import waffle.guam.community.data.jdbc.comment.QPostCommentEntity.postCommentEntity as comment
import waffle.guam.community.data.jdbc.like.QPostCommentLikeEntity.postCommentLikeEntity as likes

@Repository
class PostCommentAPIRepository(
    private val querydsl: JPAQueryFactory,
) {
    fun findCommentsOfUser(
        userId: Long? = null,
        afterCommentId: Long? = null,
        sortedByLikes: Boolean = false,
        pageSize: Long = 20L,
    ): List<MyCommentView> =
        querydsl
            .select(
                Projections.constructor(
                    MyCommentView::class.java,
                    comment.id, comment.post.id, comment.content, comment.images,
                    likes.count().`as`(sortCriteria), comment.createdAt, comment.updatedAt
                )
            )
            .from(comment)
            .leftJoin(comment.likes, likes)
            .where(
                eqUserId(userId),
                gtId(afterCommentId),
            )
            .groupBy(likes.comment.id)
            .orderBy(
                if (sortedByLikes) sortCriteria.desc()
                else comment.id.desc()
            )
            .fetch()

    private fun eqId(id: Long?) =
        id?.run { comment.id.eq(this) }

    private fun gtId(id: Long?) =
        id?.run { comment.id.gt(this) }

    private fun eqUserId(userId: Long?) =
        userId?.run { comment.user.id.eq(this) }

    var sortCriteria: NumberPath<Long> = Expressions.numberPath(Long::class.java, "sortCriteria")
}
