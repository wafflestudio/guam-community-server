package waffle.guam.community.service.command.notification

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
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
        val writer = userRepository.getById(event.writerId)
        val postCommentCreatedEvent = event.toCreatedEventEntity(writer)
        val mentionedEvents = event.toMentionEventEntity(writer)
        pushEventRepository.saveAll(mentionedEvents + postCommentCreatedEvent)
    }
}
