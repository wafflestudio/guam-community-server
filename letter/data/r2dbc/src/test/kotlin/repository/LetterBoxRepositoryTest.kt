package waffle.guam.favorite.data.repository

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import waffle.guam.letter.data.r2dbc.data.LetterBoxEntity
import waffle.guam.letter.data.r2dbc.repository.LetterBoxRepository

@SpringBootTest
internal class LetterBoxRepositoryTest @Autowired constructor(
    private val repository: LetterBoxRepository,
) {

    @Test
    fun query(): Unit = runBlocking {
        val lb = repository.find(userId = 1, pairId = 2)!!

        assertThat(lb.lowId).isEqualTo(1L)
        assertThat(lb.letters!!.size).isEqualTo(3L)
        assertThat(lb.letters!!.sortedByDescending { it.id }).isEqualTo(lb.letters)

        val lb404 = repository.find(userId = 1L, pairId = 404L)

        assertThat(lb404).isNull()
    }

    @Test
    fun query2(): Unit = runBlocking {
        val lb = repository.find(userId = 1, pairId = 2)!!
        val lastLetterId = lb.letters!!.first().id
        val lb2 = repository.find(userId = 1, pairId = 2, letterIdSmallerThan = lastLetterId)!!

        assertThat(lb.letters!!.size).isEqualTo(lb2.letters!!.size + 1)
        assertThat(lb.letters!!.drop(1)).isEqualTo(lb2.letters)

        val lb3 = repository.find(userId = 1, pairId = 2, size = 1)!!
        assertThat(lb3.letters!!.size).isEqualTo(1)
        assertThat(lb3.letters!!.first().id).isEqualTo(lastLetterId)
    }

    @Test
    fun save(): Unit = runBlocking {
        repository.save(
            LetterBoxEntity(lowId = 11, highId = 12)
        )
    }
}
