package waffle.guam.community.controller.post

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.controller.post.req.CreatePostRequest
import waffle.guam.community.service.command.post.CreatePostHandler
import waffle.guam.community.service.command.post.DeletePost
import waffle.guam.community.service.command.post.DeletePostHandler
import waffle.guam.community.service.query.post.readmodel.PostReadModel

@RequestMapping("api/v1/posts")
@RestController
class PostController(
    private val createPostHandler: CreatePostHandler,
    private val deletePostHandler: DeletePostHandler,
    private val postReadModel: PostReadModel,
) {
    @PostMapping("")
    fun createPost(
        @RequestBody req: CreatePostRequest,
    ) = createPostHandler.handle(req.toCommand(1L))

    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long,
    ) = deletePostHandler.handle(DeletePost(postId = postId, userId = 1L))

    @GetMapping("")
    fun getPosts(
        @RequestParam boardId: Long,
        @RequestParam(required = false) afterPostId: Long?,
    ) = postReadModel.getPostPreviewList(boardId = 1L, afterPostId = afterPostId)

    @GetMapping("/{postId}")
    fun getPost(
        @PathVariable postId: Long
    ) = postReadModel.getPostDetail(postId = postId)
}
