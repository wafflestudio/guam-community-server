package waffle.guam.favorite.service.infra

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions
import waffle.guam.favorite.service.Event
import waffle.guam.favorite.service.command.CommentLikeCreated
import waffle.guam.favorite.service.command.LikeCreated
import waffle.guam.favorite.service.command.ScrapCreated

interface FavoriteKafkaProducer {
    suspend fun send(event: Event)
}

// TODO: property 주입
// TODO: timeout, error handle or recover
// TODO: transcational 밖으로 빼고 싶다.
@Service
class FavoriteKafkaProducerImpl : FavoriteKafkaProducer {
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

    override suspend fun send(event: Event) {
        val topic = when (event) {
            is LikeCreated -> "post-like-create"
            is ScrapCreated -> "post-scrap-create"
            is CommentLikeCreated -> "comment-like-create"
            else -> return
        }
        val message = objectMapper.writeValueAsString(event)

        kafkaProducer.send(ProducerRecord(topic, message))
            .map { if (it.exception() != null) logger.error("kafka produce failed", it.exception()) }
            .awaitSingle()
    }
}
