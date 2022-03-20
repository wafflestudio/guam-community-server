package waffle.guam.community.data.jdbc.post

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import waffle.guam.community.data.jdbc.comment.QPostCommentEntity.postCommentEntity
import waffle.guam.community.data.jdbc.like.QPostLikeEntity.postLikeEntity
import waffle.guam.community.data.jdbc.post.QPostEntity.postEntity
import waffle.guam.community.data.jdbc.post.projection.MyPostView

@Repository
class PostAPIRepository(
    private val querydsl: JPAQueryFactory,
) {
    fun findPostsOfUser(
        userId: Long? = null,
        beforePostId: Long? = null,
        sortedByLikes: Boolean,
        pageSize: Long = 20L,
    ): List<MyPostView> = querydsl
        .select(
            Projections.constructor(
                MyPostView::class.java,
                postEntity.id, postEntity.boardId, postEntity.title, postEntity.content,
                postEntity.images, postLikeEntity.count().`as`(sortCriteria), postCommentEntity.count(), postEntity.status,
                postEntity.createdAt, postEntity.updatedAt,
            )
        )
        .from(postEntity)
        .leftJoin(postEntity.likes, postLikeEntity)
        .leftJoin(postEntity.comments, postCommentEntity)
        .where(
            eqUserId(userId),
            ltId(beforePostId),
        )
        .groupBy(postEntity.id)
        .orderBy(
            if (sortedByLikes) sortCriteria.desc()
            else postEntity.id.desc()
        )
        .limit(pageSize)
        .fetch()

    private fun eqId(id: Long?) =
        id?.run { postEntity.id.eq(this) }

    private fun ltId(id: Long?) =
        id?.run { postEntity.id.lt(this) }

    private fun eqUserId(userId: Long?) =
        userId?.run { postEntity.user.id.eq(this) }

    var sortCriteria: NumberPath<Long> = Expressions.numberPath(Long::class.java, "sortCriteria")
}
