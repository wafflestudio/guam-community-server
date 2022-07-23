package waffle.guam.community.service.command.comment

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        val (postId, userId, content, imageFilePaths, mentionIds) = command

        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFound(postId)
        val comment = post.addCommentBy(userId, content, mentionIds)

        val presignedUrls = comment.addImages(imageFilePaths)
        return PostCommentCreated(
            postId = postId,
            postUserId = post.userId,
            content = command.content,
            writerId = userId,
            mentionIds = mentionIds,
            isAnonymous = post.isAnonymous,
            preSignedUrls = presignedUrls,
        )
    }

    private fun PostEntity.addCommentBy(
        userId: Long,
        content: String,
        mentionIds: List<UserId>,
    ): PostCommentEntity {
        return PostCommentEntity(post = this, userId = userId, content = content)
            .apply { setMentionedUserIds(mentionIds) }
            .also(comments::add)
    }

    private fun PostCommentEntity.addImages(imageFilePaths: List<String>): List<String> {
        val uploadImageRequest = UploadImageList(parentId = id, type = ImageType.POST, imagePaths = imageFilePaths)
        return uploadImageListHandler.handle(uploadImageRequest)
            .also { images.addAll(it.dbPaths) }
            .preSignedUrls
    }
}

data class CreatePostComment(
    val postId: Long,
    val userId: Long,
    val content: String,
    val imageFilePaths: List<String>,
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
    val preSignedUrls: List<String>,
) : Result
