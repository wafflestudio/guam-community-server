package waffle.guam.community.service.query.comment.displayer

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.comment.PostCommentRepository
import waffle.guam.community.service.client.FavoriteService
import waffle.guam.community.service.domain.comment.MyCommentView

@Service
class UserCommentDisplayer(
    private val commentRepository: PostCommentRepository,
    private val favoriteService: FavoriteService,
) {
    fun getUserComments(userId: Long, beforeCommentId: Long?, sortByLikes: Boolean): List<MyCommentView> {
        val comments = commentRepository.findAllByUserIdAndIdLessThanOrderByIdDesc(
            userId = userId,
            beforeId = beforeCommentId ?: Long.MAX_VALUE,
            pageable = PageRequest.of(0, 20)
        ).content
        val favorites = favoriteService.getCommentFavorite(userId = userId, commentIds = comments.map { it.id })

        return comments.map { MyCommentView(it, favorites[it.id]!!) }
            .let {
                if (sortByLikes) {
                    it.sortedByDescending { it.likeCount }
                } else {
                    it
                }
            }
    }
}
