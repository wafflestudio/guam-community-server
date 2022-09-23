package waffle.guam.community.query.comment

import io.mockk.every
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.query.post.QueryTest
import waffle.guam.community.query.post.ServiceTest
import waffle.guam.community.service.CommentId
import waffle.guam.community.service.query.comment.UserCommentService
import waffle.guam.favorite.api.model.CommentFavoriteInfo

@Sql("classpath:/query/comment/test.sql")
@ServiceTest
@Transactional
internal class UserCommentServiceTest @Autowired constructor(
    private val sut: UserCommentService,
) : QueryTest() {

    @DisplayName("내가 쓴 댓글을 모아볼 수 있다")
    @ParameterizedTest
    @CsvSource("100,2", "4,1")
    fun myComments(beforeCommentId: Long, expectCount: Int) {
        // given test.sql

        // when
        val result = sut.getUserComments(userId = 2L, beforeCommentId, false)

        // then
        // sorted as desc order
        assertThat(result).hasSize(expectCount)
        assertThat(result).isSortedAccordingTo { o1, o2 -> (o2.id - o1.id).toInt() }
    }

    @DisplayName("내가 쓴 글을 좋아요 순으로 볼 수 있다")
    @Test
    fun myCommentsSortedByLikes() {
        // given test.sql
        val commentIdSlot = slot<List<CommentId>>()
        every {
            favoriteClient.getCommentInfos(2L, capture(commentIdSlot))
        } answers {
            val idAscSorted = commentIdSlot.captured.sorted()
            idAscSorted.associateWith { CommentFavoriteInfo(it, 10 - it, false) }
        }

        // when
        val result = sut.getUserComments(userId = 2L, null, true)

        // then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.id to it.likeCount }).containsExactly(
            2L to 8,
            4L to 6,
        )
    }
}
