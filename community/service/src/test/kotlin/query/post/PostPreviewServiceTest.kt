package waffle.guam.community.query.post

import io.mockk.every
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.BoardId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.category.PostCategory
import waffle.guam.community.service.domain.post.PostPreview
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.query.post.PostPreviewService
import java.util.stream.Stream

@Sql("classpath:/query/post/test.sql")
@ServiceTest
@Transactional
internal class PostPreviewServiceTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val sut: PostPreviewService,
) : QueryTest() {

    @BeforeEach
    fun additionalSetUp() {
        val postIdsSlot = slot<List<PostId>>()

        every {
            favoriteService.getPostFavorite(userId = any(), postIds = capture(postIdsSlot))
        } answers {
            postIdsSlot.captured.associateWith { stubPostFavorite(it) }
        }
    }

    @DisplayName("다양한 조건으로 게시글을 검색할 수 있다")
    @ParameterizedTest
    @MethodSource("search")
    fun getSearchedPostPreview(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        before: PostId?,
        expectedPostIds: Array<Long>,
    ) {
        // given test.sql

        // when
        val result = sut.getSearchedPostPreview(categoryId, keyword, userId, before)

        // then
        assertThat(result.content.map { it.id }).containsExactlyInAnyOrder(*expectedPostIds)
    }

    @DisplayName("좋아요를 누른 게시글을 가져오고, 좋아요 순위대로 응답한다")
    @ParameterizedTest
    @MethodSource("favorite")
    fun getFavoritePostPreviews(rankFrom: Int, expectedArray: Array<Long>) {
        // given
        val rankSlot = slot<Int>()
        every {
            favoriteService.getRankedPosts(any(), rankFrom = capture(rankSlot), any())
        } answers {
            listOf(2L, 3L, 1L).filter { it <= rankSlot.captured }
        }

        // when
        val result = sut.getFavoritePostPreviews(userId = 1L, rankFrom = rankFrom)

        // then
        // preserves fav service response order
        assertThat(result.content.map { it.id }).containsExactly(*expectedArray)
    }

    @DisplayName("유저가 스크랩한 게시글을 최신 순으로 볼 수 있다")
    @Test
    fun getUserScrappedPostPreviews() {
        // given
        every {
            favoriteService.getUserScrappedPosts(any(), any())
        } answers {
            listOf(1L, 2L)
        }

        // when
        val result = sut.getUserScrappedPostPreviews(userId = 1L, page = 0)

        // then
        // sorted id descending
        assertThat(result.content).hasSize(2)
        assertThat(result.content).isSortedAccordingTo { o1, o2 -> (o2.id - o1.id).toInt() }
    }

    @DisplayName("게시글을 최신 순으로 조회할 수 있으며 익명성을 보장한다")
    @ParameterizedTest
    @MethodSource("anonymous")
    fun getRecentPreviews(boardId: BoardId?, postIds: Array<Long>, isAnonymous: Array<Boolean>) {
        // given test.sql

        // when
        val result = sut.getRecentPreviews(userId = 1L, boardId = boardId)

        // then
        assertThat(result.content.map { it.id }).containsExactly(*postIds)
        assertThat(result.content.map { it.isAnonymous }).containsExactly(*isAnonymous)
        assertThat(result.content).isEqualTo(getAnswerPostPreviews(postIds, 1L))
    }

    private fun getAnswerPostPreviews(
        postIds: Array<PostId>,
        callerId: UserId,
    ): List<PostPreview> {
        return postRepository.findAllById(postIds.toList())
            .sortedByDescending { it.id }
            .map { post ->
                val favorite = stubPostFavorite(post.id)
                PostPreview(
                    id = post.id,
                    boardId = post.boardId,
                    title = post.title,
                    content = post.content,
                    imagePaths = post.images,
                    commentCount = post.comments.size,
                    status = post.status.name,
                    createdAt = post.createdAt,
                    updatedAt = post.updatedAt,
                    isMine = post.userId == callerId,
                    category = post.categories.firstOrNull()?.let(::PostCategory),
                    user = if (post.isAnonymous) AnonymousUser() else stubUser(post.userId),
                    likeCount = favorite.likeCnt,
                    scrapCount = favorite.scrapCnt,
                    isLiked = favorite.like,
                    isScrapped = favorite.scrap,
                )
            }
    }

    companion object {
        @JvmStatic
        fun favorite(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(3, arrayOf(2L, 3L, 1L)),
                Arguments.arguments(2, arrayOf(2L, 1L)),
                Arguments.arguments(1, arrayOf(1L)),
            )
        }

        @JvmStatic
        fun search(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(1L, "", 1L, null, arrayOf(1L, 2L)),
                Arguments.arguments(null, "keyword", 1L, null, arrayOf(2L, 3L)),
                Arguments.arguments(null, "", 1L, 3L, arrayOf(1L, 2L)),
                Arguments.arguments(null, "ㅁㄴㅇㄹ", 1L, null, arrayOf<Long>()),
            )
        }

        @JvmStatic
        fun anonymous(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(1L, arrayOf(3L, 2L), arrayOf(true, true)),
                Arguments.arguments(3L, arrayOf(1L), arrayOf(false)),
                Arguments.arguments(null, arrayOf(3L, 2L, 1L), arrayOf(true, true, false)),
            )
        }
    }
}
