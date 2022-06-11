package waffle.guam.favorite.service.domain

data class LetterBox(
    val userId: Long,
    val pair: User,
    val letters: List<Letter>,
)
