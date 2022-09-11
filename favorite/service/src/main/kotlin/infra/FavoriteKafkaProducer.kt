package waffle.guam.favorite.service.infra

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions
import waffle.guam.favorite.service.Event
import waffle.guam.favorite.service.ServiceProperties
import waffle.guam.favorite.service.command.CommentLikeCreated
import waffle.guam.favorite.service.command.LikeCreated
import waffle.guam.favorite.service.command.ScrapCreated

interface FavoriteKafkaProducer {
    fun send(event: Event)
}

// TODO: timeout, error handle or recover
@Service
class FavoriteKafkaProducerImpl(
    properties: ServiceProperties,
) : FavoriteKafkaProducer {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

    private val kafkaScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    private val kafkaProducer = ReactiveKafkaProducerTemplate<String, String>(
        SenderOptions.create(
            KafkaProperties().apply {
                this.bootstrapServers = listOf(properties.kafka.url)
                this.clientId = properties.kafka.clientId
            }
                .buildProducerProperties()
        )
    )

    override fun send(event: Event) {
        val topic = when (event) {
            is LikeCreated -> "post-like-create"
            is ScrapCreated -> "post-scrap-create"
            is CommentLikeCreated -> "comment-like-create"
            else -> return
        }

        kafkaScope.launch {
            val message = objectMapper.writeValueAsString(event)

            kafkaProducer.send(ProducerRecord(topic, message))
                .map {
                    if (it.exception() != null) {
                        logger.error("kafka produce failed", it.exception())
                    } else {
                        logger.info("kafka produce succeeded event : {}", event)
                    }
                }
                .awaitSingle()
        }
    }
}
