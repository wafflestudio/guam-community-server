package waffle.guam.community.service.command

// TODO: 푸시 이벤트 리스너
// import org.springframework.context.event.EventListener
// import org.springframework.stereotype.Service
// import org.springframework.transaction.annotation.Transactional
// import waffle.guam.community.data.jdbc.push.PushEventRepository
// import waffle.guam.community.service.command.PushEventResult
//
// @Service
// class CreatePushEventHandler(
//    private val pushEventRepository: PushEventRepository,
// ) {
//    @Transactional
//    @EventListener
//    fun pushEventCreated(event: PushEventResult) {
//        if (event.needNotNotify) return
//        pushEventRepository.saveAll(event.toPushEventEntities(event.producedUserId))
//    }
// }
//
// {
//    override val producedUserId: Long
//    get() = writerId
//
//    override val consumingUserId: Long
//    get() = postUserId
//
//    override fun toPushEventEntities(producedBy: Long): List<PushEventEntity> {
//        val postCommentCreatedEvent = this.toCreatedEventEntity(producedBy)
//        val mentionedEvents = this.toMentionEventEntity(producedBy)
//        return mentionedEvents + postCommentCreatedEvent
//    }
//
//    private fun toCreatedEventEntity(writerId: Long): PushEventEntity {
//        return PushEventEntity(
//            userId = postUserId,
//            writerId = writerId,
//            kind = PushEventEntity.Kind.POST_COMMENT,
//            body = content.take(50),
//            linkUrl = "/api/v1/posts/$postId",
//            isAnonymousEvent = isAnonymous,
//        )
//    }
//
//    private fun toMentionEventEntity(writerId: Long): List<PushEventEntity> {
//        return mentionIds.map {
//            PushEventEntity(
//                userId = it,
//                writerId = writerId,
//                kind = PushEventEntity.Kind.POST_COMMENT_MENTION,
//                body = content.take(50),
//                linkUrl = "/api/v1/posts/$postId",
//                isAnonymousEvent = isAnonymous,
//            )
//        }
//    }
