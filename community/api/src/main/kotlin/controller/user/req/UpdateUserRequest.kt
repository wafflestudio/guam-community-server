package waffle.guam.community.controller.user.req

import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.service.GuamForbidden
import waffle.guam.community.service.command.user.UpdateUser

data class UpdateUserRequest(
    val nickname: String? = null,
    val introduction: String? = null,
    val githubId: String? = null,
    val blogUrl: String? = null,
    val profileImage: MultipartFile? = null,
) {
    fun toCommand(userContextId: Long, userId: Long): UpdateUser {
        if (userContextId != userId)
            throw GuamForbidden("수정 권한이 없습니다.")

        return UpdateUser(
            userId = userId,
            nickname = nickname,
            introduction = introduction,
            githubId = githubId,
            blogUrl = blogUrl,
            profileImage = profileImage,
        )
    }
}
