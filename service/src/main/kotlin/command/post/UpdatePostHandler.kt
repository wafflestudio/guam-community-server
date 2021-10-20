package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.tag.PostTagEntity
import waffle.guam.community.data.jdbc.tag.TagRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class UpdatePostHandler(
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
) : CommandHandler<UpdatePost, PostUpdated>, PostQueryGenerator {
    override fun handle(command: UpdatePost): PostUpdated {
        val (postId, userId, title, content, tagId) = command

        val post = postRepository.findOne(postId(postId) * fetchTags())
            ?: throw Exception("POST NOT FOUND $postId")

        post.updateBy(userId = userId, title = title, content = content, tagId = tagId)

        return PostUpdated(postId = post.id, boardId = post.boardId, userId = post.userId)
    }

    private fun PostEntity.updateBy(
        userId: Long,
        title: String? = null,
        content: String? = null,
        tagId: Long? = null,
    ) {
        if (this.userId != userId) {
            throw Exception("USER $userId NOT AUTHORIZED TO UPDATE POST $id")
        }
        if (title != null) {
            updateTitle(title)
        }
        if (content != null) {
            updateContent(content)
        }
        if (tagId != null) {
            updateTag(tagId)
        }
    }

    private fun PostEntity.updateTitle(title: String) {
        this.title = title
    }

    private fun PostEntity.updateContent(content: String) {
        this.content = content
    }

    private fun PostEntity.updateTag(newTagId: Long) {
        val tag = tagRepository.findByIdOrNull(newTagId) ?: throw Exception("TAG NOT FOUND $newTagId")

        tags.removeAll { true }
        tags.add(PostTagEntity(post = this, tag = tag))
    }
}

data class UpdatePost(
    val postId: Long,
    val userId: Long,
    val title: String? = null,
    val content: String? = null,
    val tagId: Long? = null,
) : Command {
    init {
        if (title == null && content == null && tagId == null) {
            throw Exception("At least one property should be non-null value")
        }
    }
}

data class PostUpdated(
    val postId: Long,
    val boardId: Long,
    val userId: Long,
) : Result