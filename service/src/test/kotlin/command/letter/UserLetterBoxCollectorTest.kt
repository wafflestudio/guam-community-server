package waffle.guam.community.command.letter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import waffle.guam.community.data.jdbc.QueryDslConfig
import waffle.guam.community.data.jdbc.letter.LetterApiRepository
import waffle.guam.community.data.jdbc.letter.LetterRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.query.letter.UserLetterBoxCollector

@Sql("classpath:command/letter/test.sql")
@DataJpaTest
@Import(QueryDslConfig::class, LetterApiRepository::class)
class UserLetterBoxCollectorTest @Autowired constructor(
    userRepository: UserRepository,
    letterRepository: LetterRepository,
    letterApiRepository: LetterApiRepository,
) {
    private val userLetterBoxCollector = UserLetterBoxCollector(userRepository, letterRepository, letterApiRepository)

    @DisplayName("유저의 쪽지함 목록을 불러올 수 있다.")
    @Test
    fun getTest() {
        // given data.sql

        // when
        val result = userLetterBoxCollector.get(1L)

        // then
        assertThat(result.userId).isEqualTo(1L)
        assertThat(result.letterBoxes).extracting("pair.id").containsExactlyInAnyOrder(2L, 3L)
        assertThat(result.letterBoxes).extracting("latestLetter.id").containsExactlyInAnyOrder(4L, 9L)
    }
}
