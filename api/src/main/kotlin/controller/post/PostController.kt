package waffle.guam.community.controller.post

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.controller.post.req.CreatePostRequest
import waffle.guam.community.service.command.CommandDispatcher
import waffle.guam.community.service.command.post.DeletePost
import waffle.guam.community.service.query.post.readmodel.PostPreviewListReadModel

@RequestMapping("api/v1/posts")
@RestController
class PostController(
    private val dispatcher: CommandDispatcher,
    private val postPreviewListReadModel: PostPreviewListReadModel,
) {
    @PostMapping("")
    fun createPost(
        @RequestBody req: CreatePostRequest,
    ) = dispatcher.dispatch(req.toCommand(1L))

    @GetMapping("")
    fun getPosts() = postPreviewListReadModel.initData(1L)

    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long,
    ) = dispatcher.dispatch(DeletePost(postId = postId, userId = 1L))
}
