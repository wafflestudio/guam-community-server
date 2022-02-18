package waffle.guam.community.service.command.notification

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.service.command.comment.PostCommentCreated

@Service
class PostCommentNotificationHandler(
    private val pushNotifier: PushNotifier,
) {
    @EventListener
    fun postCommentCreated(event: PostCommentCreated) {
        commentCreated(event.postUserId, event.content, event.writerName, event.writerProfileImage)
        event.mentionIds.takeIf { it.isNotEmpty() }?.let {
            userMentioned(event.mentionIds, event.content, event.writerName, event.writerProfileImage)
        }
    }

    private fun commentCreated(postUserId: Long, content: String, writerName: String, writerImage: String?) {
        pushNotifier.sendPush(
            userIds = listOf(postUserId),
            title = "$writerName 님이 나의 게시글에 댓글을 남겼습니다.",
            body = content,
            imageUrl = writerImage,
        )
    }

    private fun userMentioned(userIds: List<Long>, content: String, writerName: String, writerImage: String?) {
        pushNotifier.sendPush(
            userIds = userIds,
            title = "$writerName 님이 게시글에서 나를 언급했습니다.",
            body = content,
            imageUrl = writerImage
        )
    }
}
