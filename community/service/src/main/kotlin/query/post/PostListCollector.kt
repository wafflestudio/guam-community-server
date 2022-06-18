package waffle.guam.community.service.query.post

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.post.beforePostId
import waffle.guam.community.data.jdbc.post.boardId
import waffle.guam.community.data.jdbc.post.status
import waffle.guam.community.data.jdbc.times
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.query.Collector

@Service
class PostListCollector(
    private val postRepository: PostRepository,
) : Collector<PostList, PostListCollector.Query> {
    override fun get(id: Query): PostList {
        val baseSpec = boardId(id.boardId) * status(PostEntity.Status.VALID)
        val spec = if (id.beforePostId == null) baseSpec else baseSpec * beforePostId(id.beforePostId)
        val pageable = PageRequest.of(id.page, id.size, Sort.by(Sort.Direction.DESC, "id"))

        return PostList(postRepository.findAll(spec, pageable))
    }

    data class Query(
        val boardId: Long?,
        val beforePostId: Long? = null,
        val page: Int = 0,
        val size: Int = 20,
    )
}
