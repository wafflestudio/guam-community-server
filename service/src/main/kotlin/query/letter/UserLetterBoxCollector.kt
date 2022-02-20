package waffle.guam.community.service.query.letter

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.letter.LetterApiRepository
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.letter.Letter
import waffle.guam.community.service.domain.letter.LetterBox
import waffle.guam.community.service.domain.letter.UserLetterBoxList
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.Collector

@Service
class UserLetterBoxCollector(
    private val userRepository: UserRepository,
    private val letterRepository: LetterRepository,
    private val letterApiRepository: LetterApiRepository,
) : Collector<UserLetterBoxList, UserId> {
    override fun get(id: UserId): UserLetterBoxList {
        val letterIds = letterApiRepository.findRecentLetterIdsOf(userId = id)
        val letters = letterRepository.findAllById(letterIds)

        val letterBoxMap = fetchLetterBox(id, letters)
        val pairUsers = userRepository.findAllById(letterBoxMap.keys).associateBy { it.id }

        // Pair User -> Latest Letter
        val letterBoxDto = letterBoxMap
            .mapKeys { (userId, _) -> User(pairUsers[userId]!!) }
            .mapValues { (_, letterEntity) -> Letter(letterEntity) }
            .map { (user, letter) -> LetterBox(user, letter) }

        return UserLetterBoxList(userId = id, letterBoxes = letterBoxDto)
    }

    private fun fetchLetterBox(myId: UserId, letters: List<LetterEntity>): Map<UserId, LetterEntity> {
        val map = mutableMapOf<UserId, LetterEntity>()
        letters.onEach { letter ->
            val pairId = letter.pairOf(myId)
            when (val value = map[pairId]) {
                null -> map[pairId] = letter
                else -> map[pairId] = latestOf(value, letter)
            }
        }
        return map
    }

    private fun LetterEntity.pairOf(myId: UserId) =
        if (sentBy == myId) sentTo else sentBy

    private fun latestOf(one: LetterEntity, two: LetterEntity): LetterEntity {
        val result = compareBy<LetterEntity> { it.id }.compare(one, two)
        return if (result > 0) one else two
    }
}
