package waffle.guam.favorite.service.letter

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import waffle.guam.favorite.service.ServiceTest
import waffle.guam.favorite.service.command.ClearLetterBox
import waffle.guam.favorite.service.command.CreateLetter
import waffle.guam.favorite.service.command.LetterCommandService
import waffle.guam.favorite.service.command.ReadLetterBox
import waffle.guam.favorite.service.query.LetterQueryService

@ServiceTest
class LetterServiceTest @Autowired constructor(
    private val letterCommandService: LetterCommandService,
    private val letterQueryService: LetterQueryService,
) {

    @Test
    fun `첫 쪽지를 보내면 쪽지함과 쪽지가 생성된다`(): Unit = runBlocking {
        letterCommandService.createLetter(CreateLetter(senderId = 1, receiverId = 2, text = "1", images = null))

        val letterBox = letterQueryService.getLetterBox(userId = 1, pairId = 2, size = 10)

        assertThat(letterBox).isNotNull
        letterBox!!.run {
            assertThat(letters.size).isEqualTo(1)
            assertThat(userId).isEqualTo(1L)
            assertThat(pair.id).isEqualTo(2L)
        }
        letterBox.letters[0].run {
            assertThat(sentBy).isEqualTo(1L)
            assertThat(sentTo).isEqualTo(2L)
        }
    }

    @Test
    fun `쪽지함을 조회할 때, 인자로 받은 id보다 작은 id의 쪽지만 조회된다`(): Unit = runBlocking {
        letterCommandService.createLetter(CreateLetter(senderId = 1, receiverId = 2, text = "1", images = null))
        letterCommandService.createLetter(CreateLetter(senderId = 1, receiverId = 2, text = "1", images = null))
        letterCommandService.createLetter(CreateLetter(senderId = 1, receiverId = 2, text = "1", images = null))

        val letterBox = letterQueryService.getLetterBox(userId = 1, pairId = 2, size = 10)
        val latestLetterId = letterBox!!.letters.first().id

        val letterBoxLatestLetterExcluded = letterQueryService.getLetterBox(
            userId = 1,
            pairId = 2,
            size = 10,
            letterIdSmallerThan = latestLetterId
        )

        letterBoxLatestLetterExcluded!!.run {
            assertThat(letters.size).isEqualTo(2)
            assertThat(letters.none { it.id == latestLetterId }).isTrue
        }
    }

    @Test
    fun `쪽지함을 비우면 나의 쪽지함의 쪽지가 사라진다`(): Unit = runBlocking {
        letterCommandService.createLetter(CreateLetter(senderId = 1, receiverId = 2, text = "1", images = null))
        letterCommandService.createLetter(CreateLetter(senderId = 1, receiverId = 2, text = "2", images = null))
        letterCommandService.clearLetterBox(ClearLetterBox(userId = 1, pairId = 2))

        val myLetterBox = letterQueryService.getLetterBox(
            userId = 1,
            pairId = 2,
            size = 10,
            letterIdSmallerThan = null
        )
        myLetterBox!!.run {
            assertThat(letters.size).isEqualTo(0) // 내 쪽지함의 쪽지는 비워진다.
            assertThat(userId).isEqualTo(userId)
            assertThat(pair.id).isEqualTo(2L)
        }

        val pairLetterBox = letterQueryService.getLetterBox(
            userId = 2,
            pairId = 1,
            size = 10,
            letterIdSmallerThan = null
        )
        pairLetterBox!!.run {
            assertThat(letters.size).isEqualTo(2) // 상대방의 쪽지함의 쪽지는 비워지지 않는다.
            assertThat(userId).isEqualTo(2L)
            assertThat(pair.id).isEqualTo(1L)
        }
    }

    @Test
    fun `쪽지함을 읽으면 내가 받은 쪽지가 모두 읽음 처리 된다`(): Unit = runBlocking {
        letterCommandService.createLetter(CreateLetter(senderId = 2, receiverId = 1, text = "1", images = null))
        letterCommandService.createLetter(CreateLetter(senderId = 2, receiverId = 1, text = "2", images = null))
        letterCommandService.createLetter(CreateLetter(senderId = 1, receiverId = 2, text = "3", images = null))

        val letterBox = letterQueryService.getLetterBox(userId = 1, pairId = 2, size = 10)
        letterBox!!.run {
            assertThat(letters.size).isEqualTo(3)
            assertThat(letters.none { it.isRead }).isTrue
        }

        letterCommandService.readLetterBox(ReadLetterBox(userId = 1, pairId = 2))

        val lbAfterRead = letterQueryService.getLetterBox(userId = 1, pairId = 2, size = 10)
        lbAfterRead!!.run {
            assertThat(letters.none { it.sentTo == 1L && !it.isRead }).isTrue // 내가 받은 쪽지는 모두 읽음 처리된다.
            assertThat(letters.none { it.sentTo == 2L && it.isRead }).isTrue // 상대방이 받은 쪽지는 읽음 처리되지 않는다.
        }
    }
}
