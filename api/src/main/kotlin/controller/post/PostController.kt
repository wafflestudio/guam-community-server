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
import waffle.guam.community.service.command.Event
import waffle.guam.community.service.command.post.DeletePost
import waffle.guam.community.service.query.post.readmodel.PostPreviewListReadModel

@RequestMapping("api/v1/post")
@RestController
class PostController(
    private val dispatcher: CommandDispatcher,
    private val postPreviewListReadModel: PostPreviewListReadModel
) {

    @PostMapping("")
    fun createPost(
        @RequestBody req: CreatePostRequest
    ) = when (val event = dispatcher.dispatch(req.toCommand(1L))) {
        is Event.Result -> event.value
        is Event.Error -> throw event.error
    }

    @GetMapping("")
    fun getPosts() = postPreviewListReadModel.initData(1L)

    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long
    ) = when (val event = dispatcher.dispatch(DeletePost(postId = postId, userId = 1L))) {
        is Event.Result -> event.value
        is Event.Error -> throw event.error
    }
}
