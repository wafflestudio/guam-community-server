package waffle.guam.favorite.service.query

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.r2dbc.repository.LikeRepository

// FIXME
interface LikeUserStore {
    suspend fun getLiked(postId: Long, userId: Long): Boolean
    suspend fun getLiked(postIds: List<Long>, userId: Long): Map<Long, Boolean>
}

@Primary
@Service
class LikeUserStoreImpl(
    private val likeRepository: LikeRepository,
) : LikeUserStore {
    override suspend fun getLiked(postId: Long, userId: Long): Boolean {
        return likeRepository.existsByPostIdAndUserId(postId, userId)
    }

    override suspend fun getLiked(postIds: List<Long>, userId: Long): Map<Long, Boolean> {
        return likeRepository.findAllByPostIdInAndUserId(postIds, userId)
            .toList()
            .map { it.postId }
            .toSet()
            .let { likedPostIds -> postIds.associateWith { likedPostIds.contains(it) } }
    }
}

@Service
class LikeUserStoreCacheImpl : LikeUserStore {
    override suspend fun getLiked(postId: Long, userId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getLiked(postIds: List<Long>, userId: Long): Map<Long, Boolean> {
        TODO("Not yet implemented")
    }
}
