package waffle.guam.community.service.query.comment

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.GuamCacheFactory
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.post.fetchComments
import waffle.guam.community.data.jdbc.post.postId
import waffle.guam.community.data.jdbc.post.postIds
import waffle.guam.community.data.jdbc.post.throwIfNotContainIds
import waffle.guam.community.data.jdbc.times
import waffle.guam.community.service.PostId
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.client.UserService
import waffle.guam.community.service.command.comment.PostCommentCreated
import waffle.guam.community.service.command.comment.PostCommentDeleted
import waffle.guam.community.service.command.comment.PostCommentUpdated
import waffle.guam.community.service.domain.comment.PostCommentList
import waffle.guam.community.service.query.MultiCollector

@Service
class PostCommentListCollector(
    private val postRepository: PostRepository,
    private val userService: UserService,
) : MultiCollector<PostCommentList, PostId> {
    override fun get(id: PostId): PostCommentList {
        val post = postRepository.findOne(spec = postId(id) * fetchComments()) ?: throw PostNotFound()
        val userMap = userService.multiGet(post.comments.map { it.userId })

        return PostCommentList(post, userMap)
    }

    override fun multiGet(ids: Collection<PostId>): Map<PostId, PostCommentList> =
        postRepository.findAll(spec = postIds(ids) * fetchComments())
            .also { posts -> posts.throwIfNotContainIds(ids) }
            .let { posts ->
                val userIdsInPosts = posts.flatMap { it.comments.map { comment -> comment.userId } }
                val userMapsInPosts = userService.multiGet(userIdsInPosts)

                posts.map { post ->
                    val userIds = post.comments.map { it.userId }
                    val userMap = userMapsInPosts.filter { userIds.contains(it.key) }

                    post.id to PostCommentList(post, userMap)
                }
            }
            .toMap()

    @Service
    class CacheImpl(
        private val impl: PostCommentListCollector,
        cacheFactory: GuamCacheFactory,
    ) : MultiCollector<PostCommentList, PostId> {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = cacheFactory.getCache(
            name = "POST_COMMENTS_CACHE",
            loader = impl::get,
            multiLoader = impl::multiGet
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
