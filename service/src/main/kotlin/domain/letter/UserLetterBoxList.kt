package waffle.guam.community.service.domain.letter

import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.user.User

data class UserLetterBoxList(
    val userId: UserId,
    val letterBoxes: List<LetterBox>
)

data class LetterBox(
    val pair: User,
    val latestLetter: Letter,
)
