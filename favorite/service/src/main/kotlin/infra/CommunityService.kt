package waffle.guam.favorite.service.infra

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

interface CommunityService {
    suspend fun getPost(postId: Long): Post?
    suspend fun getComment(commentId: Long): Comment?
}

@Service
class CommunityServiceImpl(
    webClientBuilder: WebClient.Builder,
) : CommunityService {
    private val community = webClientBuilder
        .baseUrl("http://guam-community.jon-snow-korea.com")
        .build()

    override suspend fun getPost(postId: Long): Post? {
        return community.get()
            .uri("/papi/v1/posts?postIds={postId}", postId)
            .retrieve()
            .awaitBody<PostResponse>()
            .posts[postId]
    }

    override suspend fun getComment(commentId: Long): Comment? {
        return community.get()
            .uri("/papi/v1/comments?commentIds={commentId}", commentId)
            .retrieve()
            .awaitBody<CommentResponse>()
            .comments[commentId]
    }

    private data class PostResponse(
        val posts: Map<Long, Post>,
    )

    private data class CommentResponse(
        val comments: Map<Long, Comment>,
    )
}

data class Post(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val status: String,
    val isAnonymous: Boolean,
)

data class Comment(
    val id: Long,
    val postId: Long,
    val userId: Long,
    val content: String,
    val status: String,
    val isAnonymous: Boolean,
)