package waffle.guam.community.service.command.comment

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.common.UserNotFound
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.data.jdbc.user.UserRepository
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
    private val userRepository: UserRepository,
    private val uploadImageListHandler: UploadImageListHandler,
) : CommandHandler<CreatePostComment, PostCommentCreated> {

    @Transactional
    override fun handle(command: CreatePostComment): PostCommentCreated {
        val (postId, userId, content, images) = command

        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFound(postId)
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFound(userId)

        post.addCommentBy(user, content, images)

        return PostCommentCreated(
            postId = postId,
            postUserId = post.user.id,
            mentionIds = command.mentionIds,
            content = command.content,
            writerName = user.nickname ?: "유저 $userId",
            writerProfileImage = user.profileImage
        )
    }

    private fun PostEntity.addCommentBy(user: UserEntity, content: String, images: List<MultipartFile>) {
        val newComment = PostCommentEntity(post = this, user = user, content = content)
        comments.add(newComment.apply { addImages(images) })
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
    val postUserId: Long,
    val mentionIds: List<UserId>,
    val content: String,
    val writerName: String,
    val writerProfileImage: String?,
) : Result
