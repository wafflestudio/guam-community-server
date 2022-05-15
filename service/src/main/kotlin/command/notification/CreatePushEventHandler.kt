package waffle.guam.community.service.command.notification

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.push.PushEventRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.comment.PostCommentCreated
import waffle.guam.community.service.command.like.PostCommentLikeCreated
import waffle.guam.community.service.command.like.PostLikeCreated
import waffle.guam.community.service.command.scrap.PostScrapCreated

@Service
class CreatePushEventHandler(
    private val pushEventRepository: PushEventRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    @EventListener
    fun postCommentCreated(event: PostCommentCreated) {
        if (event.needNotNotify) return
        val userWhoWrote = userRepository.getById(event.writerId)
        val postCommentCreatedEvent = event.toCreatedEventEntity(userWhoWrote)
        val mentionedEvents = event.toMentionEventEntity(userWhoWrote)
        pushEventRepository.saveAll(mentionedEvents + postCommentCreatedEvent)
    }

    @Transactional
    @EventListener
    fun postLiked(event: PostLikeCreated) {
        val userWhoLiked = userRepository.getById(event.userId)
        pushEventRepository.save(event.toPushEventEntity(userWhoLiked))
    }

    @Transactional
    @EventListener
    fun postCommentLiked(event: PostCommentLikeCreated) {
        val userWhoLiked = userRepository.getById(event.userId)
        pushEventRepository.save(event.toPushEventEntity(userWhoLiked))
    }

    @Transactional
    @EventListener
    fun postScrapped(event: PostScrapCreated) {
        val userWhoScrapped = userRepository.getById(event.userId)
        pushEventRepository.save(event.toPushEventEntity(userWhoScrapped))
    }
}
