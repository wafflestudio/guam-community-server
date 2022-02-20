package waffle.guam.community.service.query.letter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.common.LetterNotFound
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.domain.letter.Letter
import waffle.guam.community.service.query.Collector

@Service
class LetterCollector(
    private val letterRepository: LetterRepository,
) : Collector<Letter, LetterId> {
    override fun get(id: LetterId): Letter {
        val letter = letterRepository.findByIdOrNull(id) ?: throw LetterNotFound(id)
        return Letter(letter)
    }
}
