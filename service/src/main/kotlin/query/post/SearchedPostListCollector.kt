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
        val searchedPosts = postRepository.findAll(boardId(id.boardId) * afterPostId(id.afterPostId) * fetchTags())
            .filter { post ->
                post.tags.any { it.tag.title == id.tag } &&
                    (post.title.contains(id.keyword) || post.content.contains(id.keyword))
            }.sortedByDescending { post ->
                post.id
            }

        return PostList(
            content = searchedPosts.take(id.size).map { Post.of(it) },
            hasNext = searchedPosts.size > id.size
        )
    }

    data class Query(
        val boardId: Long,
        val tag: String,
        val keyword: String,
        val afterPostId: Long,
        val size: Int
    )
}
