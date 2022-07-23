package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.board.BoardRepository
import waffle.guam.community.data.jdbc.category.CategoryRepository
import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.BadBoardId
import waffle.guam.community.service.BadCategoryId
import waffle.guam.community.service.GuamBadRequest
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.image.ImageListUploaded
import waffle.guam.community.service.command.EventResult
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.domain.image.ImageType

@Service
class CreatePostHandler(
    private val postRepository: PostRepository,
    private val boardRepository: BoardRepository,
    private val categoryRepository: CategoryRepository,
    private val imageHandler: UploadImageListHandler,
) : CommandHandler<CreatePost, PostCreated> {

    @Transactional
    override fun handle(command: CreatePost): PostCreated {
        checkBoardId(command.boardId)
        val post = postRepository.save(command.toEntity())

        post.addCategory(command.categoryId)
        val preSignedUrls = post.addImages(command.imageFilePaths).preSignedUrls

        return PostCreated(
            postId = post.id,
            boardId = post.boardId,
            userId = post.userId,
            presignedUrls = preSignedUrls,
        )
    }

    private fun checkBoardId(boardId: Long) {
        require(boardRepository.existsById(boardId)) { throw BadBoardId(boardId) }
    }

    private fun CreatePost.toEntity() = PostEntity(
        boardId = boardId,
        userId = userId,
        title = title,
        content = content
    )

    private fun PostEntity.addCategory(categoryId: Long) {
        val category = categoryRepository.findByIdOrNull(categoryId) ?: throw BadCategoryId(categoryId)
        categories.add(PostCategoryEntity(post = this, category = category))
    }

    private fun PostEntity.addImages(images: List<String>): ImageListUploaded {
        return imageHandler.handle(
            UploadImageList(parentId = id, type = ImageType.POST, imagePaths = images)
        ).also { event ->
            this.images = event.dbPaths
        }
    }
}

data class CreatePost(
    val boardId: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val imageFilePaths: List<String>,
    val categoryId: Long,
) : Command {
    init {
        if (content.isBlank() || title.isBlank()) {
            throw GuamBadRequest("제목 또는 게시글 내용을 작성해주세요.")
        }
    }
}

data class PostCreated(
    val postId: Long,
    val boardId: Long,
    val userId: Long,
    val presignedUrls: List<String>,
) : EventResult
