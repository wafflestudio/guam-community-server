package waffle.guam.community.service.query.like

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.comment.PostCommentQueryGenerator
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.service.CommentId
import waffle.guam.community.service.command.like.PostCommentLikeCreated
import waffle.guam.community.service.command.like.PostCommentLikeDeleted
import waffle.guam.community.service.domain.like.PostCommentLikeList
import waffle.guam.community.service.query.Cache
import waffle.guam.community.service.query.MultiCollector
import java.time.Duration

@Service
class PostCommentLikeCollector(
    private val postCommentRepository: PostCommentRepository,
) : MultiCollector<PostCommentLikeList, CommentId>, PostCommentQueryGenerator {
    override fun get(id: CommentId): PostCommentLikeList {
        val comment = postCommentRepository.findOne(commentId(id) * fetchCommentLikes())
            ?: throw Exception("COMMENT NOT FOUND $id")

        return PostCommentLikeList(comment)
    }

    override fun multiGet(ids: Collection<CommentId>): Map<CommentId, PostCommentLikeList> =
        postCommentRepository.findAll(commentIdIn(ids) * fetchCommentLikes())
            .also { comments -> comments.throwIfNotContainIds(ids) }
            .associate { comment -> comment.id to PostCommentLikeList(comment) }

    @Service
    class CacheImpl(
        private val impl: PostCommentLikeCollector,
    ) : MultiCollector<PostCommentLikeList, CommentId> {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val cache = Cache<PostCommentLikeList, CommentId>(
            maximumSize = 2000,
            duration = Duration.ofMinutes(1),
            loader = { impl.get(it) },
            multiLoader = { impl.multiGet(it) }
        )

        override fun get(id: CommentId): PostCommentLikeList = cache.get(id)

        override fun multiGet(ids: Collection<CommentId>): Map<CommentId, PostCommentLikeList> = cache.multiGet(ids)

        @EventListener
        fun reload(event: PostCommentLikeCreated) {
            cache.reload(event.commentId)
            logger.info("Cache reloaded with $event")
        }

        @EventListener
        fun reload(event: PostCommentLikeDeleted) {
            cache.reload(event.commentId)
            logger.info("Cache reloaded with $event")
        }
    }
}
