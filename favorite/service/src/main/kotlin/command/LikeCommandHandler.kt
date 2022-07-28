package waffle.guam.favorite.service.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.favorite.data.r2dbc.LikeEntity
import waffle.guam.favorite.data.r2dbc.LikeRepository
import waffle.guam.favorite.service.ServiceError
import waffle.guam.favorite.service.model.Like
import waffle.guam.favorite.service.saga.LikeSaga
import java.time.Instant

@Service
class LikeCreateHandler(
    override val likeRepository: LikeRepository,
    override val likeSaga: LikeSaga,
) : LikeCommandHandler() {
    override suspend fun internalHandle(like: Like): LikeCreated {
        if (like.exists()) {
            throw DuplicateLikeException()
        }

        likeRepository.save(LikeEntity(postId = like.postId, userId = like.userId))

        return LikeCreated(like)
    }
}

@Service
class LikeDeleteHandler(
    override val likeRepository: LikeRepository,
    override val likeSaga: LikeSaga,
) : LikeCommandHandler() {

    override suspend fun internalHandle(like: Like): LikeDeleted {
        val updatedRows = likeRepository.deleteByPostIdAndUserId(postId = like.postId, userId = like.userId)

        if (updatedRows < 1) {
            throw LikeNotFoundException()
        }

        return LikeDeleted(like)
    }
}

abstract class LikeCommandHandler : CommandHandler<Like, LikeEvent> {
    abstract val likeRepository: LikeRepository
    abstract val likeSaga: LikeSaga

    @Transactional
    override suspend fun handle(command: Like): LikeEvent {
        // TODO: 더 느슨하게 불가능..?
        return internalHandle(command).also { likeSaga.handleEvent(it) }
    }

    protected suspend fun Like.exists(): Boolean {
        return likeRepository.existsByPostIdAndUserId(postId = postId, userId = userId)
    }

    protected abstract suspend fun internalHandle(like: Like): LikeEvent
}

sealed class LikeEvent(
    override val eventTime: Instant = Instant.now(),
) : Event {
    abstract val like: Like
}

data class LikeCreated(override val like: Like) : LikeEvent()
data class LikeDeleted(override val like: Like) : LikeEvent()

class DuplicateLikeException(
    override val status: Int = 409,
    override val msg: String = "이미 좋아요를 누른 게시물입니다.",
) : ServiceError()

class LikeNotFoundException(
    override val status: Int = 404,
    override val msg: String = "좋아요를 누르지 않은 게시물입니다.",
) : ServiceError()
