package waffle.guam.community.controller.post

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.post.req.CreatePostRequest
import waffle.guam.community.controller.post.req.UpdatePostRequest
import waffle.guam.community.service.command.post.CreatePostHandler
import waffle.guam.community.service.command.post.DeletePost
import waffle.guam.community.service.command.post.DeletePostHandler
import waffle.guam.community.service.command.post.UpdatePostHandler
import waffle.guam.community.service.query.post.displayer.PostDisplayer

@RequestMapping("api/v1/posts")
@RestController
class PostController(
    private val createPostHandler: CreatePostHandler,
    private val updatePostHandler: UpdatePostHandler,
    private val deletePostHandler: DeletePostHandler,
    private val postDisplayer: PostDisplayer,
) {
    @PostMapping("")
    fun createPost(
        userContext: UserContext,
        @ModelAttribute req: CreatePostRequest,
    ) = createPostHandler.handle(req.toCommand(userContext.id))

    @PatchMapping("/{postId}")
    fun updatePost(
        userContext: UserContext,
        @PathVariable postId: Long,
        @RequestBody req: UpdatePostRequest,
    ) = updatePostHandler.handle(req.toCommand(postId = postId, userId = userContext.id))

    @DeleteMapping("/{postId}")
    fun deletePost(
        userContext: UserContext,
        @PathVariable postId: Long,
    ) = deletePostHandler.handle(DeletePost(postId = postId, userId = userContext.id))

    @GetMapping("")
    fun getPosts(
        userContext: UserContext,
        @RequestParam(required = false) boardId: Long?,
        @RequestParam(required = false) afterPostId: Long?,
    ) = postDisplayer.getPostPreviewList(
        boardId = boardId ?: 0L,
        afterPostId = afterPostId,
        userId = userContext.id
    )

    @GetMapping("", params = ["keyword"])
    fun searchPosts(
        userContext: UserContext,
        @RequestParam boardId: Long,
        @RequestParam tag: String,
        @RequestParam keyword: String,
        @RequestParam(required = false) afterPostId: Long?,
    ) = postDisplayer.getSearchedPostPreviewList(
        boardId = boardId,
        tag = tag,
        keyword = keyword,
        afterPostId = afterPostId,
        userId = userContext.id,
    )

    @GetMapping("/{postId}")
    fun getPost(
        userContext: UserContext,
        @PathVariable postId: Long,
    ) = postDisplayer.getPostDetail(postId = postId, userId = userContext.id)
}
