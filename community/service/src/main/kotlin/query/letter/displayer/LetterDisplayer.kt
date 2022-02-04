package waffle.guam.community.service.query.letter.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.service.UserId
import waffle.guam.community.service.query.letter.LetterBoxCollector
import waffle.guam.community.service.query.letter.LetterBoxList
import waffle.guam.community.service.query.letter.LetterList
import waffle.guam.community.service.query.letter.LetterListCollector

@Service
class LetterDisplayer(
    private val letterListCollector: LetterListCollector,
    private val letterBoxCollector: LetterBoxCollector,
) {
    fun getMyLetterBoxes(userId: UserId): LetterBoxList =
        letterBoxCollector.get(id = userId)

    fun getLetters(userId: UserId, pairId: UserId, afterLetterId: Long, size: Long): LetterList {
        return letterListCollector.get(
            LetterListCollector.Query(
                userId = userId,
                pairId = pairId,
                afterLetterId = afterLetterId,
                size = size
            )
        )
    }
}
