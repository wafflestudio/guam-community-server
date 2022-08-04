package waffle.guam.community.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback
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
    private val kafkaProducer = KafkaTemplate<String, String>(
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to listOf("34.64.147.36:9092"),
                ProducerConfig.CLIENT_ID_CONFIG to "guam-community-dev",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
            )
        )
    )

    override suspend fun send(event: NotifyingEventResult) {
        val topic = when (event) {
            is PostCommentCreated -> "post-comment-create"
            else -> return
        }

        val message = objectMapper.writeValueAsString(event)
        kafkaProducer.send(ProducerRecord(topic, message))
            .apply {
                addCallback(object : ListenableFutureCallback<SendResult<String, String>> {
                    override fun onFailure(ex: Throwable) = logger.error("kafka produce failed", ex)
                    override fun onSuccess(result: SendResult<String, String>?) = Unit
                })
            }
    }
}
