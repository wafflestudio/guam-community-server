package waffle.guam.community.service.query.letter.displayer

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.letter.LetterApiRepository
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.letter.Letter
import waffle.guam.community.service.domain.letter.UserLetterBoxList
import waffle.guam.community.service.domain.letter.UserLetterList
import waffle.guam.community.service.query.letter.UserLetterBoxCollector

@Service
class LetterBoxDisplayer(
    private val userLetterBoxCollector: UserLetterBoxCollector,
    private val letterApiRepository: LetterApiRepository,
) {
    fun getMyLetterBoxes(userId: UserId): UserLetterBoxList {
        return userLetterBoxCollector.get(userId)
    }

    @Transactional
    fun getLetters(userId: UserId, pairId: UserId, beforeLetterId: LetterId?, size: Long): UserLetterList {
        val letters = letterApiRepository.findLetters(
            userId = userId,
            pairId = pairId,
            size = size,
            beforeLetterId = beforeLetterId,
        ).onEach(LetterEntity::read)
        return UserLetterList(userId, pairId, letters.map(::Letter))
    }
}
