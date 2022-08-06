package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.LikeCreated
import waffle.guam.favorite.service.command.LikeDeleted
import waffle.guam.favorite.service.command.LikeEvent
import waffle.guam.favorite.service.infra.FavoriteKafkaProducer
import waffle.guam.favorite.service.query.LikeCountStore

interface LikeSaga {
    suspend fun handleEvent(event: LikeEvent)
}

@Service
class LikeSagaImpl(
    private val likeCountStore: LikeCountStore.Mutable,
    private val kafka: FavoriteKafkaProducer,
) : LikeSaga {

    override suspend fun handleEvent(event: LikeEvent) {
        when (event) {
            is LikeCreated -> {
                // produce event
                kafka.send(event)
                // increment like
                likeCountStore.increment(boardId = event.post.boardId, postId = event.post.id, delta = 1.0)
            }
            is LikeDeleted -> {
                likeCountStore.increment(boardId = event.post.boardId, postId = event.post.id, delta = -1.0)
            }
        }
    }
}
