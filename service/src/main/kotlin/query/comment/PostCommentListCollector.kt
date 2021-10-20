package waffle.guam.community.service.query.comment

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostId
import waffle.guam.community.service.command.comment.PostCommentCreated
import waffle.guam.community.service.command.comment.PostCommentDeleted
import waffle.guam.community.service.command.comment.PostCommentUpdated
import waffle.guam.community.service.domain.comment.PostCommentList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.MultiCollector
import waffle.guam.community.service.query.like.PostCommentLikeCollector
import java.time.Duration

@Service
class PostCommentListCollector(
    private val postRepository: PostRepository,
    private val postCommentLikeCollector: PostCommentLikeCollector.CacheImpl,
) : MultiCollector<PostCommentList, PostId>, PostQueryGenerator {
    override fun get(id: PostId): PostCommentList =
        postRepository.findOne(spec = postId(id) * fetchComments())
            ?.let { post ->
                // to avoid multiple bag, split query into two

                val commentIds = post.comments.map { it.id }
                val likeMap = postCommentLikeCollector.multiGet(commentIds)

                return PostCommentList.of(post, likeMap)
            }
            ?: throw Exception("POST NOT FOUND ($id)")

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostCommentList> =
        postRepository.findAll(spec = postIds(ids) * fetchComments())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .let { posts ->
                // to avoid multiple bag, split query into two

                val commentIdsInPosts = posts.flatMap { it.comments.map { comment -> comment.id } }
                val likeMapsInPosts = postCommentLikeCollector.multiGet(commentIdsInPosts)

                posts.map { post ->
                    val commentIds = post.comments.map { it.id }
                    val likeMap = likeMapsInPosts.filter { commentIds.contains(it.key) }

                    post.id to PostCommentList.of(post, likeMap)
                }
            }
            .toMap()

    @Service
    class CacheImpl(
        private val impl: PostCommentListCollector,
    ) : MultiCollector<PostCommentList, PostId> {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache<PostCommentList, PostId>(
            maximumSize = 1000,
            duration = Duration.ofMinutes(1),
            loader = { impl.get(it) },
            multiLoader = { impl.multiGet(it) }
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

        @EventListener
        fun reload(event: PostCommentUpdated) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }
    }
}
