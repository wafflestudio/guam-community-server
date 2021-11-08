package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
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
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.domain.image.ImageType

@Service
class CreatePostHandler(
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository,
    private val imageHandler: UploadImageListHandler,
) : CommandHandler<CreatePost, PostCreated> {

    @Transactional
    override fun handle(command: CreatePost): PostCreated {
        val post = postRepository.save(command.toEntity())

        post.addTag(command.tagId)
        post.addImages(command.images)

        return PostCreated(postId = post.id, boardId = post.boardId, userId = post.user.id)
    }

    private fun CreatePost.toEntity() = PostEntity(
        boardId = boardId,
        user = userRepository.findByIdOrNull(userId) ?: throw UserNotFound(userId),
        title = title,
        content = content
    )

    private fun PostEntity.addTag(tagId: Long) {
        val tag = tagRepository.findByIdOrNull(tagId) ?: throw TagNotFound(tagId)
        tags.add(PostTagEntity(post = this, tag = tag))
    }

    private fun PostEntity.addImages(images: List<MultipartFile>) {
        // TODO: rollback uploaded image on error
        this.images = imageHandler.handle(
            UploadImageList(parentId = id, type = ImageType.POST, images = images)
        ).imagePaths
    }
}

data class CreatePost(
    val boardId: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val images: List<MultipartFile>,
    val tagId: Long,
) : Command

data class PostCreated(
    val postId: Long,
    val boardId: Long,
    val userId: Long,
) : Result
