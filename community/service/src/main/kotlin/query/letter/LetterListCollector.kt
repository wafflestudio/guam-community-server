package waffle.guam.community.service.query.letter

import org.springframework.stereotype.Service
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.letter.Letter
import waffle.guam.community.service.query.Collector

@Service
class LetterListCollector(
    private val letterRepository: LetterRepository
) : Collector<LetterList, LetterListCollector.Query> {
    companion object : Log

    override fun get(id: Query): LetterList {
        val letterEntities = letterRepository.find(
            userId = id.userId,
            pairId = id.pairId,
            lastLetterId = id.afterLetterId,
            size = id.size
        )
        return LetterList(letterEntities.map(::Letter))
    }

    data class Query(
        val userId: UserId,
        val pairId: UserId,
        val afterLetterId: LetterId,
        val size: Long = 50L,
    )
}

data class LetterList(
    val letters: List<Letter>
)
