package waffle.guam.community.service.domain.letter

import waffle.guam.community.data.jdbc.letter.LetterEntity

data class Letter(
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    val image: String?,
)

fun Letter(e: LetterEntity): Letter =
    Letter(
        senderId = e.senderId,
        receiverId = e.receiverId,
        text = e.text,
        image = e.image
    )
