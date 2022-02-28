package waffle.guam.community.command.notification

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyList
import org.mockito.Mockito.anyString
import org.mockito.Mockito.nullable
import org.mockito.Mockito.times
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import waffle.guam.community.service.command.comment.PostCommentCreated
import waffle.guam.community.service.command.notification.PostCommentNotificationHandler
import waffle.guam.community.service.command.notification.PushNotifier

@DataJpaTest
class PostCommentNotificationHandlerTest {
    @MockBean
    private lateinit var pushNotifier: PushNotifier

    @DisplayName("게시글에 댓글을 작성하면 푸시 알림이 발생한다.")
    @Test
    fun pushNotificationTriggered() {
        // given
        val event = PostCommentCreated(
            postId = 1L,
            postUserId = 1L,
            mentionIds = listOf(1L, 2L, 3L),
            content = "어쩔티비",
            writerName = "지혁",
            writerProfileImage = null,
        )

        // when
        val handler = PostCommentNotificationHandler(pushNotifier)
        handler.postCommentCreated(event)

        // then
        Mockito.verify(pushNotifier, times(2)).sendPush(anyList(), anyString(), anyString(), nullable(String::class.java))
    }

    @DisplayName("댓글에 멘션이 없으면 푸시가 한 번만 발생한다.")
    @Test
    fun onlyToPostUser() {
        // given
        val event = PostCommentCreated(
            postId = 1L,
            postUserId = 1L,
            mentionIds = listOf(),
            content = "어쩔티비",
            writerName = "지혁",
            writerProfileImage = null,
        )

        // when
        val handler = PostCommentNotificationHandler(pushNotifier)
        handler.postCommentCreated(event)

        // then
        Mockito.verify(pushNotifier, times(1)).sendPush(anyList(), anyString(), anyString(), nullable(String::class.java))
    }
}
