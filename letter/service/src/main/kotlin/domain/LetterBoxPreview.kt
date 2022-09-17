package waffle.guam.letter.service.domain

data class LetterBoxPreview(
    val userId: Long,
    val pair: User,
    val lastLetter: Letter?,
)
