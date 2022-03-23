package waffle.guam.community.service.query.post

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.domain.post.Post
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.query.Collector

@Service
class SearchedPostListCollector(
    private val postRepository: PostRepository,
) : Collector<PostList, SearchedPostListCollector.Query>, PostQueryGenerator {
    override fun get(id: Query): PostList {
        // FIXME: 일부 조건만으로 쿼리를 날린 후, 메모리단에서 필터링 해주는 구조. 수정 필요. (es?..)
        val spec = if (id.beforePostId == null) fetchTags() else beforePostId(id.beforePostId) * fetchTags()
        val searchedPosts = postRepository.findAll(spec)
            .filter { post ->
                (id.tagId == null || post.tags.any { it.tag.id == id.tagId }) &&
                    (post.title.contains(id.keyword) || post.content.contains(id.keyword))
            }.sortedByDescending { post ->
                post.id
            }

        return PostList(
            content = searchedPosts.take(id.size).map { Post(it) },
            hasNext = searchedPosts.size > id.size
        )
    }

    data class Query(
        val tagId: Long?,
        val keyword: String,
        val beforePostId: Long?,
        val size: Int,
    )
}
