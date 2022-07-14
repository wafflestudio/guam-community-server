package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.ScrapCreated
import waffle.guam.favorite.service.command.ScrapDeleted
import waffle.guam.favorite.service.command.ScrapEvent
import waffle.guam.favorite.service.infra.NotificationService
import waffle.guam.favorite.service.query.ScrapCountStore

interface ScrapSaga {
    suspend fun handleEvent(event: ScrapEvent)
}

@Service
class ScrapSagaImpl(
    private val scrapCountStore: ScrapCountStore.Mutable,
    private val notification: NotificationService,
) : ScrapSaga {
    override suspend fun handleEvent(event: ScrapEvent) {
        when (event) {
            is ScrapCreated -> {
                // push
                notification.notify(event)
                // increment scrap
                scrapCountStore.increment(event.scrap.postId)
            }
            is ScrapDeleted -> {
                scrapCountStore.decrement(event.scrap.postId)
            }
        }
    }
}
