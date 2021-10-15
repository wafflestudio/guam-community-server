package waffle.guam.community.service.query.like

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.command.like.PostLikeCreated
import waffle.guam.community.service.command.like.PostLikeDeleted
import waffle.guam.community.service.domain.like.PostLikeList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class PostLikeListCollector(
    private val postRepository: PostRepository,
) : MultiCollector<PostLikeList, PostId>, PostQueryGenerator {
    override fun get(id: PostId): PostLikeList =
        postRepository.findOne(spec = postId(id) * fetchLikes())
            ?.let { PostLikeList.of(it) }
            ?: throw Exception("POST NOT FOUND ($id)")

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostLikeList> =
        postRepository.findAll(spec = postIds(ids) * fetchLikes())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .map { post -> post.id to PostLikeList.of(post) }
            .toMap()

    @Service
    class CacheImpl(
        postRepository: PostRepository,
    ) : PostLikeListCollector(postRepository) {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache<PostLikeList, PostId>(
            maximumSize = 1000,
            duration = Duration.ofSeconds(30),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(id: PostId): PostLikeList = cache.get(id)

        override fun multiGet(ids: Collection<PostId>): Map<PostId, PostLikeList> = cache.multiGet(ids)

        @EventListener
        fun reload(event: PostLikeCreated) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }

        @EventListener
        fun reload(event: PostLikeDeleted) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }
    }
}
