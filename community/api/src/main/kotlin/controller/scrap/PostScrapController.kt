package waffle.guam.community.controller.scrap

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.service.command.scrap.CreatePostScrap
import waffle.guam.community.service.command.scrap.CreatePostScrapHandler
import waffle.guam.community.service.command.scrap.DeletePostScrap
import waffle.guam.community.service.command.scrap.DeletePostScrapHandler

@RequestMapping("/api/v1/posts")
@RestController
class PostScrapController(
    private val createPostScrapHandler: CreatePostScrapHandler,
    private val deletePostScrapHandler: DeletePostScrapHandler,
) {
    @PostMapping("/{postId}/scraps")
    fun create(
        userContext: UserContext,
        @PathVariable postId: Long,
    ) = createPostScrapHandler.handle(CreatePostScrap(postId = postId, userId = userContext.id))

    @DeleteMapping("/{postId}/scraps")
    fun delete(
        userContext: UserContext,
        @PathVariable postId: Long,
    ) = deletePostScrapHandler.handle(DeletePostScrap(postId = postId, userId = userContext.id))
}
