package waffle.guam.user.notification.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class GuamEventKafkaConsumer {

    @KafkaListener(
        id = "membership-changed",
        topics = ["post-like-create", "post-scrap-create", "comment-like-create"],
        groupId = "notification-consumer"
    )
    fun consumeLikeEvent(
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Payload payload: String,
    ) {
        println(topic)
        println(payload)
    }
}