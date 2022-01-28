package waffle.guam.community.controller.letter.req

import org.springframework.web.multipart.MultipartFile

data class SendLetterRequest(
    val receiverId: Long,
    val text: String,
    val image: MultipartFile? = null,
)
