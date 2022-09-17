package waffle.guam.letter.service.domain

data class Block(
    val userId: Long,
    val blockUsers: List<User>,
)
