package waffle.guam.community.query.post

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import waffle.guam.community.service.CommentId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.favorite.api.model.CommentFavoriteInfo
import waffle.guam.favorite.api.model.PostFavoriteInfo
import waffle.guam.favorite.client.GuamFavoriteClient
import waffle.guam.user.client.GuamUserClient
import waffle.guam.user.domain.UserInfo

abstract class QueryTest {

    @MockkBean
    protected lateinit var userClient: GuamUserClient.Blocking

    @MockkBean
    protected lateinit var favoriteClient: GuamFavoriteClient.Blocking

    @BeforeEach
    fun setUp() {
        val postId = slot<Long>()
        val userId = slot<Long>()
        val userIds = slot<List<Long>>()
        val commentIds = slot<List<Long>>()

        every {
            favoriteClient.getPostInfo(userId = any(), postId = capture(postId))
        } answers { stubPostFavorite(postId.captured) }

        every {
            favoriteClient.getCommentInfos(userId = any(), commentIds = capture(commentIds))
        } answers { commentIds.captured.associateWith { stubPostCommentFavorite(it) } }

        every {
            userClient.getUser(capture(userId))
        } answers { stubUser(userId.captured) }

        every {
            userClient.getUsers(capture(userIds))
        } answers { userIds.captured.associateWith { stubUser(it) } }
    }

    protected fun stubPostFavorite(postId: PostId) = PostFavoriteInfo(
        postId = postId,
        likeCnt = postId * 10,
        scrapCnt = postId * 10,
        like = (postId % 2).toInt() == 0,
        scrap = (postId % 2).toInt() == 0,
    )

    protected fun stubCommentFavorite(commentId: CommentId) = CommentFavoriteInfo(
        postCommentId = commentId,
        count = 0,
        like = false,
    )

    protected fun stubUser(userId: UserId) = UserInfo(
        id = userId,
        nickname = "",
        introduction = null,
        githubId = null,
        blogUrl = null,
        email = null,
        profileImage = null,
        interests = listOf(),
    )

    protected fun stubPostCommentFavorite(commentId: CommentId) = CommentFavoriteInfo(
        postCommentId = commentId,
        count = 0,
        like = false,
    )
}

@SpringBootTest(properties = ["cloud.aws.stack.auto=false", "cloud.aws.region.static=ap-northeast-2", "spring.cloud.vault.enabled=false"])
annotation class ServiceTest
