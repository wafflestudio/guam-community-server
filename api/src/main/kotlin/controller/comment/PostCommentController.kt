package waffle.guam.community.controller.comment

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.controller.comment.req.CreatePostCommentRequest
import waffle.guam.community.service.command.comment.CreatePostComment
import waffle.guam.community.service.command.comment.CreatePostCommentHandler
import waffle.guam.community.service.command.comment.DeletePostComment
import waffle.guam.community.service.command.comment.DeletePostCommentHandler

@RequestMapping("/api/v1/posts")
@RestController
class PostCommentController(
    private val createPostCommentHandler: CreatePostCommentHandler,
    private val deletePostCommentHandler: DeletePostCommentHandler
) {
    @PostMapping("/{postId}/comments")
    fun create(
        @PathVariable postId: Long,
        @RequestBody req: CreatePostCommentRequest
    ) = createPostCommentHandler.handle(CreatePostComment(postId = postId, userId = 1L, content = req.content))

    @PostMapping("/{postId}/comments/{commentId}")
    fun delete(
        @PathVariable postId: Long,
        @PathVariable commentId: Long
    ) = deletePostCommentHandler.handle(DeletePostComment(postId = postId, userId = 1L, commentId = commentId))
}
