package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.ScrapCreated
import waffle.guam.favorite.service.command.ScrapDeleted
import waffle.guam.favorite.service.command.ScrapEvent
import waffle.guam.favorite.service.infra.FavoriteKafkaProducer
import waffle.guam.favorite.service.query.ScrapCountStore

interface ScrapSaga {
    suspend fun handleEvent(event: ScrapEvent)
}

@Service
class ScrapSagaImpl(
    private val scrapCountStore: ScrapCountStore.Mutable,
    private val kafka: FavoriteKafkaProducer,
) : ScrapSaga {
    override suspend fun handleEvent(event: ScrapEvent) {
        when (event) {
            is ScrapCreated -> {
                // produce event
                kafka.send(event)
                // increment scrap
                scrapCountStore.increment(event.scrap.postId)
            }
            is ScrapDeleted -> {
                scrapCountStore.decrement(event.scrap.postId)
            }
        }
    }
}
