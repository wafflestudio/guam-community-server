package waffle.guam.community.data.jdbc.letter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import waffle.guam.community.data.jdbc.QueryDslConfig

@Sql("classpath:/letter/test.sql")
@DataJpaTest
@Import(QueryDslConfig::class, LetterApiRepository::class)
class LetterApiRepositoryTest @Autowired constructor(
    private val letterApiRepository: LetterApiRepository,
) {
    /**
     * 최근 쪽지란
     * - 유저가 => 상대에게 발신한 최신 쪽지
     * - 상대가 => 유저에게 발신한 최신 쪽지
     * 2가지를 의미한다.
     */
    @DisplayName("유저가 다른 모든 유저들과 주고받은 최근 쪽지들을 가져올 수 있다.")
    @Test
    fun findRecentLetterIds() {
        // given data.sql
        // 삭제된 5번 row 제외하고,
        // 위 주석의 정의에 부합하는 3, 4, 8, 9번 ID가 담겨와야 한다.

        // when
        val result = letterApiRepository.findRecentLetterIdsOf(1L)

        // then
        assertThat(result).containsExactlyInAnyOrder(3L, 4L, 8L, 9L)
    }

    @DisplayName("유저가 상대와 주고받은 쪽지를 가져올 수 있다.")
    @Test
    fun findLetters() {
        // given data.sql
        // 1번유저가 2번과 주고받은 쪽지 5개
        // 그 중 삭제된 한 건 제외하고 4개를 가져와야 한다.

        // when
        val result = letterApiRepository.findLetters(1L, 2L, 0L, size = 50)

        // then
        assertThat(result).hasSize(4)
        assertThat(result).extracting("text").containsExactly("할만하네요", "괌 개발 어떠신가요", "네 안녕하세요", "안녕하세요")
    }
}
