package waffle.guam.community.service.query.letter.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.service.LetterBoxId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.query.letter.LetterBoxList
import waffle.guam.community.service.query.letter.LetterList
import waffle.guam.community.service.query.letter.LetterListCollector
import waffle.guam.community.service.query.letter.UserLetterBoxCollector

@Service
class LetterBoxDisplayer(
    private val userLetterBoxCollector: UserLetterBoxCollector,
    private val letterListCollector: LetterListCollector,
) {
    fun getMyLetterBoxes(userId: UserId): LetterBoxList {
        return userLetterBoxCollector.get(userId)
    }

    fun getLetters(userId: UserId, letterBoxId: LetterBoxId, afterLetterId: Long, size: Long): LetterList {
        return letterListCollector.get(
            LetterListCollector.Query(
                userId = userId,
                letterBoxId = letterBoxId,
                afterLetterId = afterLetterId,
                size = size
            )
        )
    }
}
