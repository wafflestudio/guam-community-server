package waffle.guam.community.service.query.post

import org.springframework.stereotype.Service
import waffle.guam.community.service.domain.post.PostDetail
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.community.service.isNull

@Service
class PostDisplayer(
    private val postPreviewService: PostPreviewService,
    private val postDetailService: PostDetailService,
) {
    /**
     * page 값이 존재하는 경우, beforePostId는 null이어야 함
     * page 값이 null인 경우, beforePostId는 어떤 값이든 상관 없음
     */
    fun getPostPreviewList(
        boardId: Long?,
        userId: Long,
        beforePostId: Long? = null,
        page: Int? = null,
    ): PostPreviewList {
        require(page.isNull || beforePostId.isNull)

        return if (page != null) {
            postPreviewService.getRecentPreviews(userId = userId, boardId = boardId, page = page)
        } else {
            postPreviewService.getRecentPreviews(userId = userId, boardId = boardId, before = beforePostId)
        }
    }

    fun getSearchedPostPreviewList(
        categoryId: Long?,
        keyword: String,
        userId: Long,
        beforePostId: Long?,
    ): PostPreviewList {
        return postPreviewService.getSearchedPostPreview(
            userId = userId,
            categoryId = categoryId,
            keyword = keyword,
            before = beforePostId,
        )
    }

    fun getFavoritePostPreviews(userId: Long, rankFrom: Int): PostPreviewList =
        postPreviewService.getFavoritePostPreviews(userId, rankFrom)

    fun getUserPostPreviews(userId: Long, beforePostId: Long?): PostPreviewList =
        postPreviewService.getUserPostPreviews(userId, beforePostId)

    fun getUserScrappedPostPreviews(userId: Long, page: Int): PostPreviewList =
        postPreviewService.getUserScrappedPostPreviews(userId, page)

    fun getPostDetail(postId: Long, userId: Long): PostDetail =
        postDetailService.getDetail(userId, postId)
}
