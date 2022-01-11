package waffle.guam.community.controller.user.req

import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.service.command.user.UpdateUser

data class UpdateUserRequest(
    val nickname: String? = null,
    val introduction: String? = null,
    val githubId: String? = null,
    val blogUrl: String? = null,
    val profileImage: MultipartFile? = null,
) {
    fun toCommand(userId: Long): UpdateUser =
        UpdateUser(
            userId = userId,
            nickname = nickname,
            introduction = introduction,
            githubId = githubId,
            blogUrl = blogUrl,
            profileImage = profileImage,
        )
}
