package waffle.guam.community.controller.post

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.post.req.CreatePostRequest
import waffle.guam.community.service.command.post.CreatePostHandler
import waffle.guam.community.service.command.post.DeletePost
import waffle.guam.community.service.command.post.DeletePostHandler
import waffle.guam.community.service.query.post.displayer.PostDisplayer

@RequestMapping("api/v1/posts")
@RestController
class PostController(
    private val createPostHandler: CreatePostHandler,
    private val deletePostHandler: DeletePostHandler,
    private val postDisplayer: PostDisplayer,
) {
    @PostMapping("")
    fun createPost(
        userContext: UserContext,
        @ModelAttribute req: CreatePostRequest,
    ) = createPostHandler.handle(req.toCommand(1L))

    @DeleteMapping("/{postId}")
    fun deletePost(
        userContext: UserContext,
        @PathVariable postId: Long,
    ) = deletePostHandler.handle(DeletePost(postId = postId, userId = 1L))

    @GetMapping("")
    fun getPosts(
        userContext: UserContext,
        @RequestParam boardId: Long,
        @RequestParam(required = false) afterPostId: Long?,
    ) = postDisplayer.getPostPreviewList(boardId = 1L, afterPostId = afterPostId, userId = userContext.id)

    @GetMapping("", params = ["keyword"])
    fun searchPosts(
        userContext: UserContext,
        @RequestParam boardId: Long,
        @RequestParam tag: String,
        @RequestParam keyword: String,
        @RequestParam(required = false) afterPostId: Long?,
    ) = postDisplayer.getSearchedPostPreviewList(boardId = 1L, tag = tag, keyword = keyword, afterPostId = afterPostId, userId = userContext.id)

    @GetMapping("/{postId}")
    fun getPost(
        userContext: UserContext,
        @PathVariable postId: Long,
    ) = postDisplayer.getPostDetail(postId = postId)
}
