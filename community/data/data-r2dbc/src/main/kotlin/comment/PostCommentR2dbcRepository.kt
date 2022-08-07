package waffle.guam.community.data.r2dbc.comment

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import com.querydsl.core.types.Projections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.r2dbc.comment.QPostCommentEntity.postCommentEntity
import waffle.guam.community.data.r2dbc.post.QPostEntity.postEntity

interface PostCommentR2dbcRepository {
    fun findAllById(ids: List<Long>): Flow<PostCommentDto>
}

interface PostCommentDao : QuerydslR2dbcRepository<PostCommentEntity, Long>

@Service
class PostCommentR2dbcRepositoryImpl(
    private val queryFactory: PostCommentDao
) : PostCommentR2dbcRepository {

    @Transactional("transactionManager")
    override fun findAllById(ids: List<Long>): Flow<PostCommentDto> {
        return queryFactory.query { query ->
            query
                .select(
                    Projections.constructor(
                        PostCommentDto::class.java,
                        postCommentEntity.id,
                        postCommentEntity.userId,
                        postCommentEntity.content,
                        postCommentEntity.status,
                        // alias is not converted to NamingStrategy
                        // 수동으로 NamingStrategy 에 맞게 바꾸어주어야 한다
                        // RowMetadata 에서 형변환을 도와야 할 수도
                        postEntity.id.`as`("postId"),
                        postEntity.boardId.`as`("post_board_id"),
                    )
                )
                .from(postCommentEntity)
                .innerJoin(postEntity)
                .on(postEntity.id.eq(postCommentEntity.postId))
                .where(postCommentEntity.id.`in`(ids))
        }
            .all()
            .asFlow()
    }
}
