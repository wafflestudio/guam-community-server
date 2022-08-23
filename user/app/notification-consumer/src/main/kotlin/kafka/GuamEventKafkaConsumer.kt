package waffle.guam.user.notification.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import waffle.guam.user.notification.event.CommentLikeCreated
import waffle.guam.user.notification.event.NotifyingEvent
import waffle.guam.user.notification.event.PostCommentCreated
import waffle.guam.user.notification.event.PostLikeCreated
import waffle.guam.user.notification.event.PostScrapCreated
import waffle.guam.user.service.notification.NotificationCommandService

@Service
class GuamEventKafkaConsumer(
    private val commandService: NotificationCommandService,
) {

    @KafkaListener(
        id = "membership-changed",
        topics = ["post-like-create", "post-scrap-create", "comment-like-create", "post-comment-create"],
        groupId = "notification-consumer"
    )
    fun consumeLikeEvent(
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Payload payload: String,
    ) {
        val objectMapper = jacksonObjectMapper()
            .registerModules(JavaTimeModule(), KotlinModule.Builder().build())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val notification = objectMapper.readValue(payload, events[topic]!!) as NotifyingEvent
        commandService.create(notification.toRequest())
    }

    private val events: Map<String, Class<*>> = mapOf(
        "post-like-create" to PostLikeCreated::class.java,
        "post-scrap-create" to PostScrapCreated::class.java,
        "comment-like-create" to CommentLikeCreated::class.java,
        "post-comment-create" to PostCommentCreated::class.java,
    )
}
