package waffle.guam.community.service.query.tag

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.PostTagEntity
import waffle.guam.community.service.PostId
import waffle.guam.community.service.domain.tag.PostTag
import waffle.guam.community.service.domain.tag.PostTagList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class PostTagListCollector(
    private val postRepository: PostRepository,
) : MultiCollector<PostTagList, PostId>, PostQueryGenerator {
    override fun get(id: PostId): PostTagList =
        postRepository.findOne(spec = postId(id) * fetchTags())
            ?.toPostTagList()
            ?: throw Exception("POST NOT FOUND ($id)")

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostTagList> =
        postRepository.findAll(spec = postIds(ids) * fetchTags())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .map { post -> post.id to post.toPostTagList() }
            .toMap()

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
        postRepository: PostRepository,
    ) : PostTagListCollector(postRepository) {
        private val cache = Cache<PostTagList, PostId>(
            maximumSize = 1000,
            duration = Duration.ofMinutes(10),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(id: PostId): PostTagList = cache.get(id)

        override fun multiGet(ids: Collection<PostId>): Map<PostId, PostTagList> = cache.multiGet(ids)

        // TODO: reload when post updated
    }
}
