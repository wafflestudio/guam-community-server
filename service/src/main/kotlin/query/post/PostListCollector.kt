package waffle.guam.community.service.query.post

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.`in`
import waffle.guam.community.data.jdbc.eq
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostEntity_
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.query.Collector

@Service
class PostListCollector(
    private val postRepository: PostRepository
) : Collector<PostList, PostListQuery> {
    override fun get(query: PostListQuery): PostList {
        val spec = eq<PostEntity>(PostEntity_.BOARD_ID, query.boardId)
            .and(`in`<PostEntity>(PostEntity_.STATUS, listOf(PostEntity.Status.VALID)))

        val pageable = PageRequest.of(
            query.page,
            query.size,
            Sort.by(Sort.Direction.DESC, PostEntity_.ID)
        )

        return postRepository.findAll(spec, pageable)
            .map { Post.of(it) }
            .let {
                PostList(
                    content = it.content,
                    hasNext = it.hasNext()
                )
            }
    }
}

data class PostListQuery(
    val boardId: Long,
    val page: Int,
    val size: Int
)
