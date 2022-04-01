package waffle.guam.community.service.command.post

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.community.common.CategoryNotFound
import waffle.guam.community.common.Forbidden
import waffle.guam.community.common.InvalidArgumentException
import waffle.guam.community.common.PostNotFound
import waffle.guam.community.data.jdbc.category.CategoryRepository
import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.post.PostQueryGenerator
import waffle.guam.community.data.jdbc.post.PostRepository
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result

@Service
class UpdatePostHandler(
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository,
) : CommandHandler<UpdatePost, PostUpdated>, PostQueryGenerator {

    @Transactional
    override fun handle(command: UpdatePost): PostUpdated {
        val (postId, userId, title, content, boardId, categoryId) = command

        val post = postRepository.findOne(postId(postId) * fetchCategories()) ?: throw PostNotFound(postId)

        post.updateBy(userId = userId, title = title, content = content, boardId = boardId, categoryId = categoryId)

        return PostUpdated(postId = post.id, boardId = post.boardId, userId = post.user.id)
    }

    private fun PostEntity.updateBy(
        userId: Long,
        title: String? = null,
        content: String? = null,
        boardId: Long? = null,
        categoryId: Long? = null,
    ) {
        if (this.user.id != userId) {
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
        this.boardId = boardId
    }

    private fun PostEntity.updateCategory(newCategoryId: Long) {
        val category = categoryRepository.findByIdOrNull(newCategoryId) ?: throw CategoryNotFound(newCategoryId)

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
        }
    }
}

data class PostUpdated(
    val postId: Long,
    val boardId: Long,
    val userId: Long,
) : Result
