package waffle.guam.community.command.letter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.letter.LetterBoxEntity
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.letter.UpdateLetterBox
import waffle.guam.community.service.command.letter.UpdateLetterBoxHandler

@DataJpaTest
class UpdateLetterBoxHandlerTest @Autowired constructor(
    private val letterBoxRepository: LetterBoxRepository,
    private val letterRepository: LetterRepository,
    private val userRepository: UserRepository,
) {
    companion object : Log
    private val updateLetterBoxHandler = UpdateLetterBoxHandler(letterBoxRepository)

    @DisplayName("유저가 안읽음 쪽지를 읽었을 때 쪽지함의 상태를 업데이트 할 수 있다.")
    @Test
    fun updateLetterBox() {
        // given
        val (from, to) = userRepository.saveAll(
            listOf(
                UserEntity(firebaseUid = "from"),
                UserEntity(firebaseUid = "to"),
            )
        )
        val letterBox = letterBoxRepository.save(
            LetterBoxEntity(from.id, to.id)
        )
        val (_, targetLetter) = letterRepository.saveAll(
            listOf(
                LetterEntity(from.id, to.id, letterBox.id, "안녕"), // old
                LetterEntity(from.id, to.id, letterBox.id, "안녕"), // new
            )
        )

        // when
        updateLetterBoxHandler.handle(
            UpdateLetterBox(
                userId = to.id, letterBoxId = letterBox.id, lastReadLetterId = targetLetter.id
            )
        )

        // then
        assertThat(letterBox.lastReadLetterIdOf(to.id)).isEqualTo(targetLetter.id)
    }
}
