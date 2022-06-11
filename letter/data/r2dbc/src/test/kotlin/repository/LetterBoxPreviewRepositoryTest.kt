package waffle.guam.favorite.data.repository

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import waffle.guam.letter.data.r2dbc.repository.LetterBoxPreviewRepository

@SpringBootTest
internal class LetterBoxPreviewRepositoryTest @Autowired constructor(
    private val repository: LetterBoxPreviewRepository,
) {

    @Test
    fun query(): Unit = runBlocking {
        val letterBox1 = repository.findAll(1L)
            .first { it.highId == 2L }

        val letterBox2 = repository.findAll(2L)
            .first { it.lowId == 1L }

        assertThat(letterBox1.id).isEqualTo(letterBox2.id)
        assertThat(letterBox1.lastLetterEntity).isEqualTo(letterBox2.lastLetterEntity)
    }
}
