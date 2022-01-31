package waffle.guam.community.service.query.letter.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.query.letter.LetterBoxList
import waffle.guam.community.service.query.letter.UserLetterBoxCollector

@Service
class LetterBoxDisplayer(
    private val letterBoxRepository: LetterBoxRepository,
    private val userLetterBoxCollector: UserLetterBoxCollector,
) {
    fun getMyLetterBoxes(userId: UserId): LetterBoxList {
        return userLetterBoxCollector.get(userId)
    }

}
