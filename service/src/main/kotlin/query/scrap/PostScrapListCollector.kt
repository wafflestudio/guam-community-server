package waffle.guam.community.service.query.scrap

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.GuamCacheFactory
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.scrap.PostScrapEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.command.scrap.PostScrapCreated
import waffle.guam.community.service.command.scrap.PostScrapDeleted
import waffle.guam.community.service.domain.scrap.PostScrap
import waffle.guam.community.service.domain.scrap.PostScrapList
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class PostScrapListCollector(
    private val postRepository: PostRepository,
) : MultiCollector<PostScrapList, PostId>, PostQueryGenerator {
    override fun get(id: PostId): PostScrapList =
        postRepository.findOne(spec = postId(id) * fetchScraps())
            ?.toPostScrapList()
            ?: throw PostNotFound()

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostScrapList> =
        postRepository.findAll(spec = postIds(ids) * fetchScraps())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .associate { post -> post.id to post.toPostScrapList() }

    private fun PostEntity.toPostScrapList() = PostScrapList(
        postId = id,
        content = scraps.map(::PostScrap)
    )

    private fun PostScrap(e: PostScrapEntity): PostScrap =
        PostScrap(postId = e.post.id, userId = e.user.id)

    @Service
    class CacheImpl(
        val impl: PostScrapListCollector,
        cacheFactory: GuamCacheFactory,
    ) : MultiCollector<PostScrapList, PostId> {
        private val cache = cacheFactory.getCache(
            name = "POST_SCRAPS_CACHE",
            ttl = Duration.ofMinutes(10),
            loader = impl::get,
            multiLoader = impl::multiGet,
        )

        override fun get(id: PostId): PostScrapList = cache.get(id)

        override fun multiGet(ids: Collection<PostId>): Map<PostId, PostScrapList> = cache.multiGet(ids)

        @EventListener
        fun scrapCreated(event: PostScrapCreated) {
            cache.reload(event.postId)
        }

        @EventListener
        fun scrapDeleted(event: PostScrapDeleted) {
            cache.reload(event.postId)
        }
    }
}
