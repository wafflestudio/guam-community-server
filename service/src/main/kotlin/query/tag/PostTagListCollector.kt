package waffle.guam.community.service.query.tag

import org.springframework.stereotype.Service
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.GuamCacheFactory
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.PostTagEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.tag.PostTag
import waffle.guam.community.service.domain.tag.PostTagList
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class PostTagListCollector(
    private val postRepository: PostRepository,
) : MultiCollector<PostTagList, PostId>, PostQueryGenerator {
    override fun get(id: PostId): PostTagList =
        postRepository.findOne(spec = postId(id) * fetchTags())
            ?.toPostTagList()
            ?: throw PostNotFound(id)

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostTagList> =
        postRepository.findAll(spec = postIds(ids) * fetchTags())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .associate { post -> post.id to post.toPostTagList() }

    private fun PostEntity.toPostTagList() = PostTagList(
        postId = id,
        content = tags.map { it.toPostTag() }
    )

    private fun PostTagEntity.toPostTag() = PostTag(
        postId = post.id,
        tagId = tag.id,
        title = tag.title
    )

    @Service
    class CacheImpl(
        val impl: PostTagListCollector,
        cacheFactory: GuamCacheFactory,
    ) : MultiCollector<PostTagList, PostId> {
        private val cache = cacheFactory.getCache(
            name = "POST_TAGS_CACHE",
            ttl = Duration.ofMinutes(10),
            loader = impl::get,
            multiLoader = impl::multiGet
        )

        override fun get(id: PostId): PostTagList = cache.get(id)

        override fun multiGet(ids: Collection<PostId>): Map<PostId, PostTagList> = cache.multiGet(ids)

        // TODO: reload when post updated
    }
}
