package waffle.guam.community.controller.post

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.UserContext
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.community.service.query.post.PostDisplayer

@RestController("/api/v1/users")
class UserPostController(
    private val postDisplay: PostDisplayer,
) {
    @GetMapping("/{userId}/posts/my")
    fun userPosts(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = false) beforePostId: Long?,
    ): PostPreviewList = postDisplay.getUserPostPreviews(userId, beforePostId)

    @GetMapping("/{userId}/posts/scrapped")
    fun userScrappedPosts(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = false) page: Int?,
    ): PostPreviewList = postDisplay.getUserScrappedPostPreviews(userId, page = page ?: 0)
}
