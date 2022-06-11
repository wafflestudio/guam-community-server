package waffle.guam.favorite.service.query

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import waffle.guam.favorite.data.r2dbc.ScrapRepository

interface ScrapUserStore {
    suspend fun getScraped(postId: Long, userId: Long): Boolean
    suspend fun getScraped(postIds: List<Long>, userId: Long): Map<Long, Boolean>
}

@Primary
@Service
class ScrapUserStoreImpl(
    private val scrapRepository: ScrapRepository,
) : ScrapUserStore {
    override suspend fun getScraped(postId: Long, userId: Long): Boolean {
        return scrapRepository.existsByPostIdAndUserId(postId, userId)
    }

    override suspend fun getScraped(postIds: List<Long>, userId: Long): Map<Long, Boolean> {
        return scrapRepository.findAllByPostIdInAndUserId(postIds, userId)
            .toList()
            .map { it.postId }
            .toSet()
            .let { scrapedPostIds -> postIds.associateWith { scrapedPostIds.contains(it) } }
    }
}

@Service
class ScrapUserStoreCacheImpl : ScrapUserStore {
    override suspend fun getScraped(postId: Long, userId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getScraped(postIds: List<Long>, userId: Long): Map<Long, Boolean> {
        TODO("Not yet implemented")
    }
}
