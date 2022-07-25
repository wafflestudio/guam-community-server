package waffle.guam.user.api.controller

import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Min

data class UpdateUserRequest(
    @Min(value = 2, message = "Nickname should be greater than 1.")
    val nickname: String? = null,
    val introduction: String? = null,
    val githubId: String? = null,
    val blogUrl: String? = null,
    val profileImage: MultipartFile? = null,
)

data class CreateInterestRequest(
    val name: String,
)
