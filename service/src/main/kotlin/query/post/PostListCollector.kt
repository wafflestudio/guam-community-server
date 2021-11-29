package waffle.guam.community.service.query.post

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostEntity_
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.query.Collector

@Service
class PostListCollector(
    private val postRepository: PostRepository,
) : Collector<PostList, PostListCollector.Query>, PostQueryGenerator {
    override fun get(id: Query): PostList {
        val spec = boardId(id.boardId) * status(PostEntity.Status.VALID) * afterPostId(id.afterPostId)
        val pageable = PageRequest.of(0, id.size, Sort.by(Sort.Direction.DESC, PostEntity_.ID))

        return postRepository.findAll(spec, pageable)
            .let { PostList.of(it) }
    }

    data class Query(
        val boardId: Long,
        val afterPostId: Long,
        val size: Int,
    )
}
