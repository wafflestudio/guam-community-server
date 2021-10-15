package waffle.guam.community.controller.like

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.service.command.like.CreatePostLike
import waffle.guam.community.service.command.like.CreatePostLikeHandler
import waffle.guam.community.service.command.like.DeletePostLike
import waffle.guam.community.service.command.like.DeletePostLikeHandler

@RequestMapping("/api/v1/posts")
@RestController
class PostLikeController(
    private val createPostLikeHandler: CreatePostLikeHandler,
    private val deletePostLikeHandler: DeletePostLikeHandler
) {
    @PostMapping("/{postId}/likes")
    fun create(
        @PathVariable postId: Long,
    ) = createPostLikeHandler.handle(CreatePostLike(postId = postId, userId = 1L))

    @DeleteMapping("/{postId}/likes")
    fun delete(
        @PathVariable postId: Long
    ) = deletePostLikeHandler.handle(DeletePostLike(postId = postId, userId = 1L))
}
