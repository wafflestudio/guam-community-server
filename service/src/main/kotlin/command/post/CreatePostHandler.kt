package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.PostTagEntity
import waffle.guam.community.data.jdbc.tag.TagRepository
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.TagNotFound
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class CreatePostHandler(
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository,
) : CommandHandler<CreatePost, PostCreated> {

    @Transactional
    override fun handle(command: CreatePost): PostCreated {
        val tag = tagRepository.findByIdOrNull(command.tagId) ?: throw TagNotFound(command.tagId)

        val post = postRepository.save(command.toEntity())

        post.tags.add(PostTagEntity(post = post, tag = tag))

        return PostCreated(postId = post.id, boardId = post.boardId, userId = post.user.id)
    }

    private fun CreatePost.toEntity() = PostEntity(
        boardId = boardId,
        user = userRepository.findByIdOrNull(userId) ?: throw UserNotFound(userId),
        title = title,
        content = content,
        // TODO
        images = emptyList()
    )
}

data class CreatePost(
    val boardId: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val tagId: Long,
) : Command

data class PostCreated(
    val postId: Long,
    val boardId: Long,
    val userId: Long,
) : Result
