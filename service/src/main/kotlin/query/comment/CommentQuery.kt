package waffle.guam.community.service.query.comment

import org.springframework.data.jpa.domain.Specification
import waffle.guam.community.data.jdbc.comment.CommentEntity
import waffle.guam.community.service.query.Query
import javax.persistence.criteria.CriteriaBuilder

class CommentQuery(
    val ids: List<Long>,
) : Query<CommentEntity> {
    override val spec: Specification<CommentEntity>
        get() = CommentSpec.run {
            all().and(ids?.let { ids(it) })
        }

    private object CommentSpec {
        fun all(): Specification<CommentEntity> = Specification { _, _, builder: CriteriaBuilder ->
            builder.conjunction()
        }

        fun ids(ids: List<Long>): Specification<CommentEntity> = Specification { root, _, builder ->
            root.get<Long>("id").`in`(ids)
        }
    }
}
