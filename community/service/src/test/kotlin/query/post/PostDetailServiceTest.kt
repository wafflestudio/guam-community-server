package waffle.guam.community.query.post

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.post.PostDetail
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.query.post.PostDetailService

@Sql("classpath:/query/post/test.sql")
@ServiceTest
internal class PostDetailServiceTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val sut: PostDetailService,
) : QueryTest() {

    @DisplayName("게시글 상세를 불러올 수 있다")
    @ParameterizedTest
    @CsvSource("1,true,false", "2,false,true", "777,false,false")
    @Transactional
    fun getDetail(
        callerId: Long,
        expectedIsMine: Boolean,
        expectedCommentIsMine: Boolean,
    ) {
        // given test.sql

        // when
        val result = sut.getDetail(userId = callerId, postId = 1L)
            .copy(comments = emptyList(), commentCount = 0)

        // then

        assertThat(result).isEqualTo(
            getAnswerWithoutComments(
                postId = 1L,
                writerId = 1L,
                expectedIsMine = expectedIsMine,
            )
        )
    }

    @DisplayName("익명처리가 잘 된다")
    @Test
    fun anonymousPostDetail() {
        // given test.sql

        // when
        val result = sut.getDetail(userId = 2L, postId = 2L)
            .copy(comments = emptyList(), commentCount = 0)

        // then
        assertThat(result).isEqualTo(
            getAnswerWithoutComments(
                postId = 2L,
                writerId = 0L,
                expectedIsMine = false,
            )
        )
    }

    private fun getAnswerWithoutComments(
        postId: Long,
        writerId: Long,
        expectedIsMine: Boolean,
    ): PostDetail {
        val expectedPost = postRepository.findByIdOrNull(postId)!!
        val expectedPostWriter = if (writerId != 0L) {
            stubUser(writerId)
        } else AnonymousUser()
        val expectedFavorite = stubPostFavorite(postId)

        return PostDetail(
            id = expectedPost.id,
            boardId = expectedPost.boardId,
            user = expectedPostWriter,
            title = expectedPost.title,
            content = expectedPost.content,
            imagePaths = expectedPost.images,
            category = PostCategory(postId, 1L, "Programming"),
            status = expectedPost.status.name,
            createdAt = expectedPost.createdAt,
            updatedAt = expectedPost.updatedAt,
            isMine = expectedIsMine,
            isLiked = expectedFavorite.like,
            likeCount = expectedFavorite.likeCnt,
            isScrapped = expectedFavorite.scrap,
            scrapCount = expectedFavorite.scrapCnt,
            commentCount = 0,
            comments = emptyList(),
        )
    }
}
