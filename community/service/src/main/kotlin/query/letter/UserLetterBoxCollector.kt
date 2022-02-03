package waffle.guam.community.service.query.letter

import org.springframework.stereotype.Service
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.letter.LetterBox
import waffle.guam.community.service.query.Collector

@Service
class UserLetterBoxCollector(
    private val letterBoxRepository: LetterBoxRepository,
) : Collector<LetterBoxList, UserId> {
    companion object : Log

    // TODO 인메모리 필터링 존재
    override fun get(id: UserId): LetterBoxList {
        val relatedBoxIds = letterBoxRepository.findBoxIds(userId = id)
        val letterBoxes = letterBoxRepository.findPreviews(relatedBoxIds)
            .filter { (letterBoxEntity, _) -> letterBoxEntity.visibleTo(id) }
            .map { (letterBox, latestLetter) ->
                LetterBox(letterBoxEntity = letterBox, latestLetter = latestLetter, userId = id)
            }
        return LetterBoxList(userId = id, letterBoxes = letterBoxes)
    }
}

data class LetterBoxList(
    val userId: UserId,
    val letterBoxes: List<LetterBox>,
)
