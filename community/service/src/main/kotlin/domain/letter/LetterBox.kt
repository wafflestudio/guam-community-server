package waffle.guam.community.service.domain.letter

import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.UserId

data class LetterBox(
    val pairId: UserId,
    val pairNickName: String,
    val pairProfileImage: String?,
    val latestLetter: Letter,
    val isLastUnread: Boolean,
)

fun LetterBox(pairUser: UserEntity, latestLetter: LetterEntity): LetterBox {
    // 상대가 보낸 쪽지를 아직 읽지 않았을 경우
    val isLastUnread = pairUser.id != latestLetter.receiverId && !latestLetter.isRead
    return LetterBox(
        pairId = pairUser.id,
        pairNickName = pairUser.nickname ?: "유저 ${pairUser.id}",
        pairProfileImage = pairUser.profileImage,
        latestLetter = Letter(latestLetter),
        isLastUnread = isLastUnread,
    )
}
