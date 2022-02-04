package waffle.guam.community.service.query.letter

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.letter.LetterBox
import waffle.guam.community.service.query.Collector

@Service
class LetterBoxCollector(
    private val letterRepository: LetterRepository,
    private val userRepository: UserRepository,
) : Collector<LetterBoxList, UserId> {
    override fun get(id: UserId): LetterBoxList {
        val letterBoxList = letterRepository.findLetterBoxes(userId = id)
            .associate { it.latestLetterId to getPairIdOf(idPair = it.pairId, myId = id) }

        val latestLetters = letterRepository.findAllById(letterBoxList.keys).associateBy { it.id }
        val pairUsers = userRepository.findAllById(letterBoxList.values).associateBy { it.id }

        return LetterBoxList(
            userId = id,
            letterBoxes = letterBoxList.map { (letterId, pairUserId) ->
                val pair = pairUsers[pairUserId]!!
                val latestLetter = latestLetters[letterId]!!
                LetterBox(pair, latestLetter)
            }
        )
    }

    private fun getPairIdOf(idPair: String, myId: UserId): Long {
        val regex = "[0-9]".toRegex()
        val (low, high) = regex.findAll(idPair).map { it.value.toLong() }.toList()
        return if (low == myId) high else low
    }
}

data class LetterBoxList(
    val userId: UserId,
    val letterBoxes: List<LetterBox>,
)
