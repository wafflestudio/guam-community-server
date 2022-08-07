package waffle.guam.community.controller.papi

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import waffle.guam.community.data.r2dbc.comment.PostCommentR2dbcRepository
import waffle.guam.community.data.r2dbc.post.PostR2dbcRepository

// TODO: security 적용
@Component
class PapiHandler(
    private val postRepository: PostR2dbcRepository,
    private val commentRepository: PostCommentR2dbcRepository,
    private val objectMapper: ObjectMapper,
) {
    suspend fun getPosts(
        request: ServerRequest
    ): ServerResponse = runBlocking {
        // FIXME 이렇게 하는게 진짜 맞나?
        val postIds = request.queryParamOrNull("postIds")
            ?.let { objectMapper.readValue(it, List::class.java) }
            ?.map { (it as Int).toLong() }
            ?: throw IllegalArgumentException("postIds are required")

        val result = postRepository.findAllById(postIds)
            .toList()
            .map {
                PostGetResponse(
                    id = it.id,
                    boardId = it.boardId,
                    userId = it.userId,
                    title = it.title,
                    content = it.content,
                    status = it.status.name,
                    isAnonymous = it.boardId == 1L
                )
            }
            .associateBy { it.id }
            .let(::PostsGetResponse)

        ServerResponse.ok().bodyValueAndAwait(result)
    }

    suspend fun getComments(
        request: ServerRequest
    ): ServerResponse {
        val commentIds = request.queryParamOrNull("commentIds")
            ?.let { objectMapper.readValue(it, List::class.java) }
            ?.map { (it as Int).toLong() }
            ?: throw IllegalArgumentException("commentIds are required")

        val result = commentRepository.findAllById(commentIds)
            .toList()
            .map {
                CommentGetResponse(
                    id = it.id,
                    postId = it.postId,
                    userId = it.userId,
                    content = it.content,
                    status = it.status.name,
                    isAnonymous = it.isAnonymous
                )
            }
            .associateBy { it.id }
            .let(::CommentsGetResponse)

        return ServerResponse.ok().bodyValueAndAwait(result)
    }

    data class PostsGetResponse(
        val posts: Map<Long, PostGetResponse>,
    )

    data class PostGetResponse(
        val id: Long,
        val boardId: Long,
        val userId: Long,
        val title: String,
        val content: String,
        val status: String,
        val isAnonymous: Boolean,
    )

    data class CommentsGetResponse(
        val comments: Map<Long, CommentGetResponse>,
    )

    data class CommentGetResponse(
        val id: Long,
        val postId: Long,
        val userId: Long,
        val content: String,
        val status: String,
        val isAnonymous: Boolean,
    )
}
