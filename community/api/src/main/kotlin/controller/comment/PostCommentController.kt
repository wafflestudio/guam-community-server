package waffle.guam.community.controller.comment

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.UserContext
import waffle.guam.community.controller.comment.req.CreatePostCommentRequest
import waffle.guam.community.service.command.comment.CreatePostCommentHandler
import waffle.guam.community.service.command.comment.DeletePostComment
import waffle.guam.community.service.command.comment.DeletePostCommentHandler
import waffle.guam.community.service.query.comment.PostCommentService

@RequestMapping("/api/v1/posts")
@RestController
class PostCommentController(
    private val createPostCommentHandler: CreatePostCommentHandler,
    private val deletePostCommentHandler: DeletePostCommentHandler,
    private val postCommentService: PostCommentService,
) {
    @GetMapping("/{postId}/comments")
    fun get(
        userContext: UserContext,
        @PathVariable postId: Long,
    ) = postCommentService.fetchPostCommentList(postId, userContext.id)

    @PostMapping("/{postId}/comments")
    fun create(
        userContext: UserContext,
        @PathVariable postId: Long,
        @RequestBody req: CreatePostCommentRequest
    ) = createPostCommentHandler.handle(req.toCommand(postId, userContext.id))

    @DeleteMapping("/{postId}/comments/{commentId}")
    fun delete(
        userContext: UserContext,
        @PathVariable postId: Long,
        @PathVariable commentId: Long
    ) = deletePostCommentHandler.handle(DeletePostComment(postId = postId, userId = userContext.id, commentId = commentId))
}
