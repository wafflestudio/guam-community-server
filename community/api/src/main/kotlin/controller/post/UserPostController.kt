package waffle.guam.community.controller.post

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.UserContext
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.community.service.query.post.displayer.PostDisplayer

@RestController
class UserPostController(
    private val postDisplay: PostDisplayer,
) {
    @GetMapping("/api/v1/users/{userId}/posts")
    fun userPosts(
        userContext: UserContext,
        @PathVariable userId: Long,
        @RequestParam(required = false) beforePostId: Long?,
    ): PostPreviewList = postDisplay.getUserPostList(userId, beforePostId)
}
