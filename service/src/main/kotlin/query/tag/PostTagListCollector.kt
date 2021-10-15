package waffle.guam.community.service.query.tag

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.tag.PostTagRepository
import waffle.guam.community.service.domain.tag.PostTag
import waffle.guam.community.service.domain.tag.PostTagList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class PostTagListCollector(
    private val postTagRepository: PostTagRepository,
) : Collector<PostTagList, Long> {
    override fun get(postId: Long): PostTagList =
        postTagRepository.findAllByPostId(postId)
            .map { PostTag(it.tag.id, it.tag.title) }
            .let { PostTagList(postId = postId, content = it) }

    fun multiGet(postIds: Collection<Long>): Map<Long, PostTagList> {
        val tagMap = postTagRepository.findAllByPostIdIn(postIds)
            .groupBy { it.post.id }
            .mapValues { it.value.map { PostTag(it.tag.id, it.tag.title) } }

        return postIds.map { it to PostTagList(postId = it, content = tagMap[it] ?: emptyList()) }
            .toMap()
    }

    @Service
    class CacheImpl(
        postTagRepository: PostTagRepository,
    ) : PostTagListCollector(postTagRepository) {
        private val cache = Cache<PostTagList, Long>(
            maximumSize = 1000,
            duration = Duration.ofMinutes(10),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(postId: Long): PostTagList = cache.get(postId)

        override fun multiGet(postIds: Collection<Long>): Map<Long, PostTagList> = cache.multiGet(postIds)

        // TODO: reload when post updated
    }
}
