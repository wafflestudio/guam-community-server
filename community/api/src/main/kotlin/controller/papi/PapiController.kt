package waffle.guam.community.controller.papi

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.data.jdbc.post.PostRepository

// TODO: security 적용
@RequestMapping("/papi/v1")
@RestController
class PapiController(
    private val postRepository: PostRepository,
    private val commentRepository: PostCommentRepository,
) {

    @GetMapping("/posts")
    fun getPosts(
        @RequestParam postIds: List<Long>,
    ): PostsGetResponse {
        return postRepository.findAllById(postIds)
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
    }

    @GetMapping("/comments")
    fun getComments(
        @RequestParam commentIds: List<Long>,
    ): CommentsGetResponse {
        return commentRepository.findAllById(commentIds)
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
