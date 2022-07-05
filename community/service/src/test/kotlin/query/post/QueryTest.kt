package waffle.guam.community.query.post

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import waffle.guam.community.service.CommentId
import waffle.guam.community.service.PostId
import waffle.guam.community.service.UserId
import waffle.guam.community.service.client.CommentFavorite
import waffle.guam.community.service.client.FavoriteService
import waffle.guam.community.service.client.PostFavorite
import waffle.guam.community.service.client.UserService
import waffle.guam.community.service.domain.user.User

abstract class QueryTest {

    @MockkBean
    protected lateinit var userService: UserService

    @MockkBean
    protected lateinit var favoriteService: FavoriteService

    @BeforeEach
    fun setUp() {
        val postId = slot<Long>()
        val userId = slot<Long>()
        val userIds = slot<List<Long>>()
        val commentIds = slot<List<Long>>()

        every {
            favoriteService.getPostFavorite(userId = any(), postId = capture(postId))
        } answers { stubPostFavorite(postId.captured) }

        every {
            favoriteService.getCommentFavorite(userId = any(), commentIds = capture(commentIds))
        } answers { commentIds.captured.associateWith { stubPostCommentFavorite(it) } }

        every {
            userService.get(capture(userId))
        } answers { stubUser(userId.captured) }

        every {
            userService.multiGet(capture(userIds))
        } answers { userIds.captured.associateWith { stubUser(it) } }
    }

    protected fun stubPostFavorite(postId: PostId) = PostFavorite(
        postId = postId,
        likeCnt = (postId * 10).toInt(),
        scrapCnt = (postId * 10).toInt(),
        like = (postId % 2).toInt() == 0,
        scrap = (postId % 2).toInt() == 0,
    )

    protected fun stubCommentFavorite(commentId: CommentId) = CommentFavorite(
        postCommentId = commentId,
        count = 0,
        like = false,
    )

    protected fun stubUser(userId: UserId) = User(
        id = userId,
        nickname = "",
        introduction = null,
        githubId = null,
        blogUrl = null,
        email = null,
        profileImage = null,
        interests = listOf(),
    )

    protected fun stubPostCommentFavorite(commentId: CommentId) = CommentFavorite(
        postCommentId = commentId,
        count = 0,
        like = false,
    )
}

@SpringBootTest(properties = ["cloud.aws.stack.auto=false", "cloud.aws.region.static=ap-northeast-2", "spring.cloud.vault.enabled=false"])
annotation class ServiceTest
