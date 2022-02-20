package waffle.guam.community.service.domain.letter

import waffle.guam.community.service.UserId

data class UserLetterList(
    val userId: UserId,
    val pairId: UserId,
    val letters: List<Letter>,
)
