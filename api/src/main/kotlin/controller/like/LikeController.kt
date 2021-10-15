package waffle.guam.community.controller.like

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.service.command.CommandDispatcher
import waffle.guam.community.service.command.like.CreatePostLike
import waffle.guam.community.service.command.like.DeletePostLike

@RequestMapping("/api/v1/likes")
@RestController
class LikeController(
    private val dispatcher: CommandDispatcher
) {
    @PostMapping("/posts/{postId}")
    fun likePost(
        @PathVariable postId: Long,
    ) = dispatcher.dispatch(CreatePostLike(postId = postId, userId = 1L))

    @DeleteMapping("/posts/{postId}")
    fun unlikePost(
        @PathVariable postId: Long
    ) = dispatcher.dispatch(DeletePostLike(postId = postId, userId = 1L))
}
