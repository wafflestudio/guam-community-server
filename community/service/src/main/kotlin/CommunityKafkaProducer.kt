package waffle.guam.community.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions
import waffle.guam.community.service.command.NotifyingEventResult
import waffle.guam.community.service.command.comment.PostCommentCreated

interface CommunityKafkaProducer {
    suspend fun send(event: NotifyingEventResult)
}

// TODO: @see FavoriteKafkaProducer
@Service
class CommunityKafkaProducerImpl : CommunityKafkaProducer {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val objectMapper = jacksonObjectMapper().also { it.registerModule(JavaTimeModule()) }
    private val kafkaProducer = ReactiveKafkaProducerTemplate<String, String>(
        SenderOptions.create(
            KafkaProperties().apply {
                this.bootstrapServers = listOf("34.64.147.36:9092")
                this.clientId = "guam-favorite-dev"
            }
                .buildProducerProperties()
        )
    )

    override suspend fun send(event: NotifyingEventResult) {
        val topic = when (event) {
            is PostCommentCreated -> "post-comment-created"
            else -> return
        }

        val message = objectMapper.writeValueAsString(event)
        kafkaProducer.send(ProducerRecord(topic, message))
            .map { if (it.exception() != null) logger.error("kafka produce failed", it.exception()) }
            .awaitSingle()
    }
}
