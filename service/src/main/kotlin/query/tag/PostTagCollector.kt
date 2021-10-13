package waffle.guam.community.service.query.tag

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.tag.PostTagRepository
import waffle.guam.community.service.domain.tag.Tag
import waffle.guam.community.service.domain.tag.TagList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class PostTagCollector(
    private val postTagRepository: PostTagRepository,
) : Collector<TagList, Long> {
    override fun get(postId: Long): TagList =
        postTagRepository.findAllByPostId(postId)
            .map { Tag(it.tag.id, it.tag.title) }
            .let(::TagList)

    fun multiGet(postIds: Collection<Long>): Map<Long, TagList> {
        val tagMap = postTagRepository.findAllByPostIdIn(postIds)
            .groupBy { it.post.id }
            .mapValues { it.value.map { Tag(it.tag.id, it.tag.title) } }

        return postIds.map { it to TagList(tagMap[it] ?: emptyList()) }
            .toMap()
    }

    @Service
    class CacheImpl(
        postTagRepository: PostTagRepository,
    ) : PostTagCollector(postTagRepository) {
        private val cache = Cache<TagList, Long>(
            maximumSize = 500,
            duration = Duration.ofMinutes(10),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(postId: Long): TagList = cache.get(postId)

        override fun multiGet(postIds: Collection<Long>): Map<Long, TagList> = cache.multiGet(postIds)
    }
}
