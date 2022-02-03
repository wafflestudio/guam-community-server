package waffle.guam.community.command.letter

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.letter.LetterBoxEntity
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.letter.BlockLetterBox
import waffle.guam.community.service.command.letter.BlockLetterBoxHandler

@DataJpaTest
class BlockLetterBoxHandlerTest @Autowired constructor(
    private val letterBoxRepository: LetterBoxRepository,
    private val userRepository: UserRepository,
) {
    companion object : Log
    private val blockLetterBoxHandler = BlockLetterBoxHandler(letterBoxRepository)

    @DisplayName("유저가 상대를 차단할 수 있다.")
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

        // when
        blockLetterBoxHandler.handle(
            BlockLetterBox(userId = to.id, letterBoxId = letterBox.id)
        )

        // then
        // 차단 시, 쪽지함이 보이지 않도록 삭제된다
        assertTrue(letterBox.isDeletedBy(to.id))
        assertTrue(letterBox.hasBlockedOther(to.id))
        assertTrue(letterBox.isBlockedByOther(from.id))
    }
}
