package waffle.guam.community.query.comment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.query.post.QueryTest
import waffle.guam.community.query.post.ServiceTest
import waffle.guam.community.service.CommentId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.comment.PostCommentDetail
import waffle.guam.community.service.domain.user.AnonymousUser
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.comment.PostCommentService

@Sql("classpath:/query/comment/test.sql")
@ServiceTest
@Transactional
internal class PostCommentServiceTest @Autowired constructor(
    private val postRepository: PostRepository,
    private val sut: PostCommentService
) : QueryTest() {
    @DisplayName("댓글 상세 목록을 불러올 수 있다")
    @Test
    fun getCommentDetailList() {
        // given test.sql

        // when
        val result = sut.fetchPostCommentList(postId = 1L, callerId = 1L).content

        // then
        assertThat(result).isEqualTo(
            getAnswer(
                postId = 1L,
                callerId = 1L,
                users = mapOf(
                    1L to stubUser(1L),
                    2L to stubUser(2L),
                )
            )
        )
    }

    @DisplayName("익명 처리가 잘 된다")
    @Test
    fun anonymousComments() {
        // given test.sql

        // when
        val result = sut.fetchPostCommentList(postId = 2L, callerId = 1L).content

        // then
        assertThat(result).isEqualTo(
            getAnswer(
                postId = 2L,
                callerId = 1L,
                users = mapOf(
                    3L to AnonymousUser("(글쓴이)"),
                    4L to AnonymousUser("1"),
                    5L to AnonymousUser("2"),
                )
            )
        )
    }

    private fun getAnswer(postId: PostId, callerId: UserId, users: Map<CommentId, User>): List<PostCommentDetail> {
        return postRepository.getById(postId).comments
            .filter { it.status == PostCommentEntity.Status.VALID }
            .sortedByDescending { it.id }
            .map {
                val fav = stubCommentFavorite(it.id)
                PostCommentDetail(
                    postId = it.post.id,
                    id = it.id,
                    user = users[it.id]!!,
                    content = it.content,
                    imagePaths = it.images,
                    mentionIds = it.mentionIds,
                    likeCount = fav.count,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    isMine = it.userId == callerId,
                    isLiked = fav.like,
                )
            }
    }
}
