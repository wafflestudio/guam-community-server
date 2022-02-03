package waffle.guam.community.command.letter

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.letter.LetterBoxEntity
import waffle.guam.community.data.jdbc.letter.LetterBoxRepository
import waffle.guam.community.data.jdbc.report.ReportEntity
import waffle.guam.community.data.jdbc.report.ReportRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.letter.ReportLetterBox
import waffle.guam.community.service.command.letter.ReportLetterBoxHandler

@DataJpaTest
class ReportLetterBoxHandlerTest @Autowired constructor(
    private val letterBoxRepository: LetterBoxRepository,
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
) {
    companion object : Log
    private val reportLetterBoxHandler = ReportLetterBoxHandler(letterBoxRepository, reportRepository)

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
        reportLetterBoxHandler.handle(
            ReportLetterBox(
                reporterId = from.id, suspectId = to.id, letterBoxId = letterBox.id, reportType = ReportEntity.Type.SPAMMING
            )
        )

        // then
        // 신고 시, 상대방을 차단하고 쪽지함을 삭제한다.
        assertTrue(letterBox.isReported)
        assertTrue(letterBox.isDeletedBy(from.id))
        assertTrue(letterBox.hasBlockedOther(from.id))
    }
}
