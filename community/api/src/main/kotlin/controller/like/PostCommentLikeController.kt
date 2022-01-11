package waffle.guam.community.controller.like

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.service.command.like.CreatePostCommentLike
import waffle.guam.community.service.command.like.CreatePostCommentLikeHandler
import waffle.guam.community.service.command.like.DeletePostCommentLike
import waffle.guam.community.service.command.like.DeletePostCommentLikeHandler

@RequestMapping("/api/v1/posts")
@RestController
class PostCommentLikeController(
    private val createPostCommentLikeHandler: CreatePostCommentLikeHandler,
    private val deletePostCommentLikeHandler: DeletePostCommentLikeHandler,
) {
    @PostMapping("/{postId}/comments/{commentId}/likes")
    fun create(
        userContext: UserContext,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
    ) = createPostCommentLikeHandler.handle(CreatePostCommentLike(postId = postId, commentId = commentId, userId = 1L))

    @DeleteMapping("/{postId}/comments/{commentId}/likes")
    fun delete(
        userContext: UserContext,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
    ) = deletePostCommentLikeHandler.handle(DeletePostCommentLike(postId = postId, commentId = commentId, userId = 1L))
}
