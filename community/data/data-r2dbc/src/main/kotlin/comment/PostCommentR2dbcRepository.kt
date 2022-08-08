package waffle.guam.community.data.r2dbc.comment

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service

interface PostCommentR2dbcRepository {
    fun findAllById(ids: List<Long>): Flow<PostCommentDto>
}

@Service
class PostCommentR2dbcRepositoryImpl(
    private val dbClient: DatabaseClient,
) : PostCommentR2dbcRepository {
    override fun findAllById(ids: List<Long>): Flow<PostCommentDto> {
        return dbClient.sql("""
            SELECT pc.*, p.board_id as board_id FROM post_comments pc
            inner join posts p on pc.post_id = p.id
            where pc.id in (:ids)
        """)
            .bind("ids", ids)
            .map(::PostCommentDto)
            .flow()
    }

    private fun PostCommentDto(row: Row): PostCommentDto {
        return PostCommentDto(
            id = (row.get("id") as Number).toLong(),
            userId = (row.get("user_id") as Number).toLong(),
            content = row.get("content", String::class.java)!!,
            status = row.get("status", String::class.java)!!.let(PostCommentEntity.Status::valueOf),
            postId = (row.get("post_id") as Number).toLong(),
            postBoardId = (row.get("board_id") as Number).toLong(),
        )
    }
}
