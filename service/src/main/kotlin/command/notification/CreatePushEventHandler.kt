package waffle.guam.community.service.command.notification

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.push.PushEventEntity
import waffle.guam.community.data.jdbc.push.PushEventRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.comment.PostCommentCreated

@Service
class CreatePushEventHandler(
    private val pushEventRepository: PushEventRepository,
    private val userRepository: UserRepository,
) {
    @EventListener
    fun postCommentCreated(event: PostCommentCreated) {
        val writer = userRepository.findById(event.writerId).get()

        pushEventRepository.save(
            PushEventEntity(
                userId = event.postUserId,
                writer = writer,
                kind = PushEventEntity.Kind.POST_COMMENT,
                body = event.content,
                linkUrl = "/api/v1/posts/${event.postId}",
                isRead = false
            )
        )

        if (event.mentionIds.isNotEmpty()) {
            val mentionedEvents = event.mentionIds.map {
                PushEventEntity(
                    userId = it,
                    writer = writer,
                    kind = PushEventEntity.Kind.POST_COMMENT_MENTION,
                    body = event.content,
                    linkUrl = "/api/v1/posts/${event.postId}",
                    isRead = false
                )
            }

            pushEventRepository.saveAll(mentionedEvents)
        }
    }
}
