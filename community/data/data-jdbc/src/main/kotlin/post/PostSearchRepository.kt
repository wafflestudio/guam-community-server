package waffle.guam.community.data.jdbc.post

import org.hibernate.search.engine.search.query.SearchResult
import org.hibernate.search.mapper.orm.Search
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import javax.persistence.EntityManager

@Component
class PostSearchRepository(
    private val entityManager: EntityManager,
) {
    fun search(query: String, beforePostId: Long?, categoryId: Long?, pageable: Pageable): GuamSearchResult<PostEntity> {
        val searchSession = Search.session(entityManager)
        val scope = searchSession.scope(PostEntity::class.java)
        return searchSession
            .search(scope)
            .where { predicateFactory ->
                predicateFactory.bool { boolPredicates ->
                    boolPredicates
                        .must(
                            predicateFactory
                                .match()
                                .fields("title", "content")
                                .matching(query)
                        )
                        .must(
                            predicateFactory
                                .match()
                                .field("status")
                                .matching(PostEntity.Status.VALID)
                        )
                    beforePostId?.let {
                        boolPredicates.must(
                            predicateFactory
                                .range()
                                .field("id")
                                .lessThan(beforePostId)
                        )
                    }
                    categoryId?.let {
                        boolPredicates.must(
                            predicateFactory
                                .match()
                                .field("categories.id")
                                .matching(categoryId)
                        )
                    }
                }
            }
            .sort { sortFactory ->
                sortFactory.field("id").desc()
            }
            .fetch(pageable.offset.toInt(), pageable.pageSize)
            .toGuamDto()
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> SearchResult<*>.toGuamDto() =
    GuamSearchResult(
        this.total().hitCount(),
        this.hits(),
    ) as GuamSearchResult<T>

data class GuamSearchResult<T>(
    val totalCount: Long,
    val result: List<T>,
)
