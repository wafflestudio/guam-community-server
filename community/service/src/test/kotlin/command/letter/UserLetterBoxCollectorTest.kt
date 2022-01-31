package waffle.guam.community.command.letter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import waffle.guam.community.data.jdbc.letter.LetterBoxEntity
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.query.letter.UserLetterBoxCollector

@DataJpaTest
class UserLetterBoxCollectorTest @Autowired constructor(
    private val letterRepository: LetterRepository,
    private val letterBoxRepository: LetterBoxRepository,
    private val userRepository: UserRepository,
) {
    val collector = UserLetterBoxCollector(letterBoxRepository)

    @DisplayName("내가 참여 중인 쪽지함 정보를 가져올 수 있다")
    @Test
    fun getMyLetterBoxes() {
        // given
        val (sender, receiver, nobody) = userRepository.saveAll(
            listOf(
                UserEntity(firebaseUid = "me"),
                UserEntity(firebaseUid = "someOne"),
                UserEntity(firebaseUid = "nobody"),
            )
        )
        val (letterBoxOne, letterBoxTwo) = letterBoxRepository.saveAll(
            listOf(
                LetterBoxEntity(senderId = sender.id, receiverId = receiver.id),
                LetterBoxEntity(senderId = nobody.id, receiverId = receiver.id),
            )
        )
        val (_, answerOne, answerTwo) = letterRepository.saveAll(
            listOf(
                LetterEntity(sender.id, receiver.id, letterBoxOne.id, "하이"), // not latest, unread
                LetterEntity(sender.id, receiver.id, letterBoxOne.id, "정답"), // latest, unread
                LetterEntity(nobody.id, receiver.id, letterBoxTwo.id, "정답"), // latest, read!!
            )
        )
        // 읽음 처리가 되었다고 가정
        letterBoxTwo
            .apply { setLastReadLetterIdOf(receiver.id, answerTwo.id) }
            .let(letterBoxRepository::save)

        // when
        val result = collector.get(receiver.id)

        // then
        assertThat(result.userId).isEqualTo(receiver.id)
        assertThat(result.letterBoxes).hasSize(2)

        val (resultOne, resultTwo) = result.letterBoxes
        assertThat(resultOne.latestLetter.text).isEqualTo(answerOne.text)
        assertThat(resultOne.isLastUnread).isEqualTo(true)

        assertThat(resultTwo.latestLetter.text).isEqualTo(answerTwo.text)
        assertThat(resultTwo.isLastUnread).isEqualTo(false)
    }
}
