package waffle.guam.community.service.command.comment

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.UserId
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.domain.image.ImageType

@Service
class CreatePostCommentHandler(
    private val postRepository: PostRepository,
    private val uploadImageListHandler: UploadImageListHandler,
) : CommandHandler<CreatePostComment, PostCommentCreated> {

    @Transactional
    override fun handle(command: CreatePostComment): PostCommentCreated {
        val (postId, userId, content, images, mentionIds) = command

        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFound(postId)

        post.addCommentBy(userId, content, images, mentionIds)

        return PostCommentCreated(
            postId = postId,
            postUserId = post.userId,
            content = command.content,
            writerId = userId,
            mentionIds = mentionIds,
            isAnonymous = post.isAnonymous,
        )
    }

    private fun PostEntity.addCommentBy(
        userId: Long,
        content: String,
        images: List<MultipartFile>,
        mentionIds: List<UserId>,
    ) {
        PostCommentEntity(post = this, userId = userId, content = content)
            .apply { addImages(images) }
            .apply { setMentionedUserIds(mentionIds) }
            .let(comments::add)
    }

    // TODO: rollback uploaded image on error
    private fun PostCommentEntity.addImages(imageList: List<MultipartFile>) {
        val uploadImageRequest = UploadImageList(parentId = id, type = ImageType.POST, images = imageList)
        images.addAll(uploadImageListHandler.handle(uploadImageRequest).imagePaths)
    }
}

data class CreatePostComment(
    val postId: Long,
    val userId: Long,
    val content: String,
    val images: List<MultipartFile>,
    val mentionIds: List<UserId>,
) : Command

data class PostCommentCreated(
    val postId: Long,
    @get:JsonIgnore
    val postUserId: Long,
    val mentionIds: List<UserId>,
    val content: String,
    @get:JsonIgnore
    val isAnonymous: Boolean,
    val writerId: Long,
) : Result
