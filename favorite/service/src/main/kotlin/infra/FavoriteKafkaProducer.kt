package waffle.guam.favorite.service.infra

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions
import waffle.guam.favorite.service.command.CommentLikeCreated
import waffle.guam.favorite.service.command.Event
import waffle.guam.favorite.service.command.LikeCreated
import waffle.guam.favorite.service.command.ScrapCreated

interface FavoriteKafkaProducer {
    suspend fun send(event: Event)
}

// TODO: property 주입
@Service
class FavoriteKafkaProducerImpl : FavoriteKafkaProducer {
    private val objectMapper = jacksonObjectMapper()
    private val kafkaProducer = ReactiveKafkaProducerTemplate<String, String>(
        SenderOptions.create(
            KafkaProperties().apply {
                this.bootstrapServers = listOf("34.64.147.36:30182")
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

        kafkaProducer.send(ProducerRecord(topic, message)).awaitSingle()
    }
}
