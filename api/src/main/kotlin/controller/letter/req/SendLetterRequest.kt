package waffle.guam.community.controller.letter.req

import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.service.UserId

data class SendLetterRequest(
    val to: UserId,
    val text: String,
    val image: MultipartFile?,
)
