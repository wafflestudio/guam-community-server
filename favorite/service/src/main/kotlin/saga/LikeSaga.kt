package waffle.guam.favorite.service.saga

import org.springframework.stereotype.Service
import waffle.guam.favorite.service.command.LikeCreated
import waffle.guam.favorite.service.command.LikeDeleted
import waffle.guam.favorite.service.command.LikeEvent
import waffle.guam.favorite.service.query.LikeCountStore

interface LikeSaga {
    suspend fun handleEvent(event: LikeEvent)
}

@Service
class LikeSagaImpl(
    private val likeCountStore: LikeCountStore.Mutable,
) : LikeSaga {
    override suspend fun handleEvent(event: LikeEvent) {
        when (event) {
            is LikeCreated -> {
                likeCountStore.increment(event.like.postId)
            }
            is LikeDeleted -> {
                likeCountStore.decrement(event.like.postId)
            }
        }
    }
}
