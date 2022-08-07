package waffle.guam.community.controller.papi

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.data.jdbc.post.PostRepository

// TODO: security 적용
@Component
class PapiHandler(
    private val postRepository: PostRepository,
    private val commentRepository: PostCommentRepository,
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

        // FIXME use R2dbc
        val result = postRepository.findAllById(postIds)
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
            .map {
                CommentGetResponse(
                    id = it.id,
                    postId = it.post.id,
                    userId = it.userId,
                    content = it.content,
                    status = it.status.name,
                    isAnonymous = it.post.boardId == 1L // FIXME: N + 1
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
