package waffle.guam.community.service.query.comment

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.command.comment.PostCommentCreated
import waffle.guam.community.service.command.comment.PostCommentDeleted
import waffle.guam.community.service.domain.comment.PostCommentList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class PostCommentListCollector(
    private val postRepository: PostRepository,
) : MultiCollector<PostCommentList, PostId>, PostQueryGenerator {
    override fun get(id: PostId): PostCommentList =
        postRepository.findOne(spec = postId(id) * fetchComments())
            ?.let { PostCommentList.of(it) }
            ?: throw Exception("POST NOT FOUND ($id)")

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostCommentList> =
        postRepository.findAll(spec = postIds(ids) * fetchComments())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .map { post -> post.id to PostCommentList.of(post) }
            .toMap()

    @Service
    class CacheImpl(
        postRepository: PostRepository,
    ) : PostCommentListCollector(postRepository) {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache<PostCommentList, PostId>(
            maximumSize = 1000,
            duration = Duration.ofSeconds(30),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(id: PostId): PostCommentList = cache.get(id)

        override fun multiGet(ids: Collection<PostId>): Map<PostId, PostCommentList> = cache.multiGet(ids)

        @EventListener
        fun reload(event: PostCommentCreated) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }

        @EventListener
        fun reload(event: PostCommentDeleted) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }

        // TODO: reload when comment updated
    }
}
