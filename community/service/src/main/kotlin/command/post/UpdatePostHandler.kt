package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.data.jdbc.board.BoardRepository
import waffle.guam.community.data.jdbc.category.CategoryRepository
import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.BadBoardId
import waffle.guam.community.service.BadCategoryId
import waffle.guam.community.service.Forbidden
import waffle.guam.community.service.GuamBadRequest
import waffle.guam.community.service.InvalidArgumentException
import waffle.guam.community.service.PostNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class UpdatePostHandler(
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository,
    private val boardRepository: BoardRepository,
) : CommandHandler<UpdatePost, PostUpdated>, PostQueryGenerator {

    @Transactional
    override fun handle(command: UpdatePost): PostUpdated {
        val post =
            postRepository.findOne(postId(command.postId) * fetchCategories()) ?: throw PostNotFound(command.postId)
        post.updateBy(command)

        return PostUpdated(postId = post.id, boardId = post.boardId, userId = post.userId)
    }

    private fun PostEntity.updateBy(command: UpdatePost) {
        val (_, userId, title, content, boardId, categoryId) = command

        if (this.userId != userId) {
            throw Forbidden("USER $userId NOT AUTHORIZED TO UPDATE POST $id")
        }
        if (title != null) {
            updateTitle(title)
        }
        if (content != null) {
            updateContent(content)
        }
        if (boardId != null) {
            updateBoardId(boardId)
        }
        if (categoryId != null) {
            updateCategory(categoryId)
        }
    }

    private fun PostEntity.updateTitle(title: String) {
        this.title = title
    }

    private fun PostEntity.updateContent(content: String) {
        this.content = content
    }

    private fun PostEntity.updateBoardId(boardId: Long) {
        val newBoard = boardRepository.findByIdOrNull(boardId) ?: throw BadBoardId(boardId)

        val isChangingAnonymity = newBoard.isAnonymous xor this.isAnonymous
        if (isChangingAnonymity) {
            throw Forbidden("게시판을 이동할 수 없습니다.")
        }

        this.boardId = boardId
    }

    private fun PostEntity.updateCategory(newCategoryId: Long) {
        val category = categoryRepository.findByIdOrNull(newCategoryId) ?: throw BadCategoryId(newCategoryId)

        categories.removeAll { true }
        categories.add(PostCategoryEntity(post = this, category = category))
    }
}

data class UpdatePost(
    val postId: Long,
    val userId: Long,
    val title: String? = null,
    val content: String? = null,
    val boardId: Long? = null,
    val categoryId: Long? = null,
) : Command {
    init {
        if (title == null && content == null && categoryId == null && boardId == null) {
            throw InvalidArgumentException("적어도 한 개 이상의 필드값을 변경해야합니다.")
        } else if (content?.isBlank() == true || title?.isBlank() == true) {
            throw GuamBadRequest("제목이나 게시글 내용은 빈 값으로 변경할 수 없습니다.")
        }
    }
}

data class PostUpdated(
    val postId: Long,
    val boardId: Long,
    val userId: Long,
) : Result
