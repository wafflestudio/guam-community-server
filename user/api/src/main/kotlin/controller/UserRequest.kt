package waffle.guam.user.api.controller

import org.springframework.web.multipart.MultipartFile

data class UpdateUserRequest(
    val nickname: String? = null,
    val introduction: String? = null,
    val githubId: String? = null,
    val blogUrl: String? = null,
    val profileImage: MultipartFile? = null,
)

data class CreateInterestRequest(
    val name: String,
)
