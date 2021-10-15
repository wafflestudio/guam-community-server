package waffle.guam.community.service.query.comment

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.service.command.comment.PostCommentCreated
import waffle.guam.community.service.domain.comment.PostComment
import waffle.guam.community.service.domain.comment.PostCommentList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.Collector
import java.time.Duration

@Service
class PostCommentListCollector(
    private val postCommentRepository: PostCommentRepository,
) : Collector<PostCommentList, Long> {
    override fun get(postId: Long): PostCommentList =
        postCommentRepository.findAllByPostId(postId)
            .map { PostComment.of(it) }
            .let { PostCommentList(postId = postId, content = it) }

    fun multiGet(postIds: Collection<Long>): Map<Long, PostCommentList> {
        val contentMap = postCommentRepository.findAllByPostIdIn(postIds)
            .groupBy { it.post.id }
            .mapValues { it.value.map { PostComment.of(it) } }

        return postIds.map { it to PostCommentList(postId = it, content = contentMap[it] ?: emptyList()) }
            .toMap()
    }

    @Service
    class CacheImpl(
        postCommentRepository: PostCommentRepository
    ) : PostCommentListCollector(postCommentRepository) {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache<PostCommentList, Long>(
            maximumSize = 1000,
            duration = Duration.ofSeconds(10),
            loader = { super.get(it) },
            multiLoader = { super.multiGet(it) }
        )

        override fun get(postId: Long): PostCommentList = cache.get(postId)

        override fun multiGet(postIds: Collection<Long>): Map<Long, PostCommentList> = cache.multiGet(postIds)

        @EventListener
        fun reload(event: PostCommentCreated) {
            cache.reload(event.postId)
            logger.info("Cache reloaded with $event")
        }
    }
}
