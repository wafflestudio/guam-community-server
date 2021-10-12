package waffle.guam.community.service.query.comment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import waffle.guam.community.data.jdbc.comment.CommentRepository
import waffle.guam.community.service.model.Comment
import waffle.guam.community.service.model.Comment.Companion.toDomain
import waffle.guam.community.service.query.Collector

class CommentCollector(
    private val commentRepository: CommentRepository,
) : Collector<CommentQuery, Comment> {
    override fun get(query: CommentQuery): Comment =
        commentRepository.findAll(query.spec).firstOrNull()?.toDomain() ?: throw Exception()

    override fun gets(query: CommentQuery): List<Comment> =
        commentRepository.findAll(query.spec).map { it.toDomain() }

    override fun gets(query: CommentQuery, pageable: Pageable): Page<Comment> =
        commentRepository.findAll(query.spec, pageable).map { it.toDomain() }
}
