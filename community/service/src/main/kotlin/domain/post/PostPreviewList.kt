package waffle.guam.community.service.domain.post

class PostPreviewList(
    val content: List<PostPreview>,
    val hasNext: Boolean
)

class SearchedPostPreviewList(
    val totalCount: Long,
    val content: List<PostPreview>,
    val hasNext: Boolean,
)

fun SearchedPostPreviewList(
    totalCount: Long,
    result: PostPreviewList,
): SearchedPostPreviewList {
    return SearchedPostPreviewList(
        totalCount, result.content, result.hasNext
    )
}
