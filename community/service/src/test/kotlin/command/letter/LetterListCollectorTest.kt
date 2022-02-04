package waffle.guam.community.command.letter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import waffle.guam.community.data.jdbc.letter.LetterEntity
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.query.letter.LetterListCollector

@DataJpaTest
class LetterListCollectorTest @Autowired constructor(
    private val letterRepository: LetterRepository,
    private val userRepository: UserRepository,
) {
    private val collector = LetterListCollector(letterRepository)

    @DisplayName("특정 쪽지함에서 유저가 주고받은 쪽지들을 확인할 수 있다.")
    @Test
    fun getLetters() {
        // given
        val (me, other) = userRepository.saveAll(
            listOf(
                UserEntity(firebaseUid = "me"),
                UserEntity(firebaseUid = "other"),
            )
        )
        val letters = letterRepository.saveAll(
            listOf(
                LetterEntity(me.id, other.id, "안녕하세요"),
                LetterEntity(other.id, me.id, "네 안녕하세요"),
                LetterEntity(me.id, other.id, "반갑습니다"),
                LetterEntity(other.id, me.id, "반가워요"),
            )
        )

        // when
        val result = collector.get(
            LetterListCollector.Query(
                userId = me.id,
                pairId = other.id,
                afterLetterId = 0L,
                size = 3L,
            )
        )

        // then
        assertThat(result.letters).hasSize(3)

        val (latest, middle, oldest) = result.letters
        assertThat(latest.text).isEqualTo("반가워요")
        assertThat(middle.text).isEqualTo("반갑습니다")
        assertThat(oldest.text).isEqualTo("네 안녕하세요")
        assertThat(latest.senderId).isEqualTo(other.id)
        assertThat(latest.receiverId).isEqualTo(me.id)
    }
}
