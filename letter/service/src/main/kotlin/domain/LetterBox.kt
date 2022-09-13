package waffle.guam.letter.service.domain

data class LetterBox(
    val id: Long,
    val userId: Long,
    val pair: User,
    val letters: List<Letter>,
)
