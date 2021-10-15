package waffle.guam.community.service.query.post.readmodel

import org.springframework.stereotype.Service
import waffle.guam.community.service.domain.post.PostList
import waffle.guam.community.service.domain.post.PostPreview
import waffle.guam.community.service.domain.post.PostPreviewList
import waffle.guam.community.service.query.like.PostLikesCollector
import waffle.guam.community.service.query.post.PostListCollector
import waffle.guam.community.service.query.post.PostListQuery
import waffle.guam.community.service.query.post.RecentPostListCollector
import waffle.guam.community.service.query.tag.PostTagCollector
import waffle.guam.community.service.query.user.UserCollector

@Service
class PostPreviewListReadModel(
    private val postListCollector: PostListCollector,
    private val recentPostListCollector: RecentPostListCollector.CacheImpl,
    private val userCollector: UserCollector.CacheImpl,
    private val postTagCollector: PostTagCollector.CacheImpl,
    private val postLikesCollector: PostLikesCollector.CacheImpl,
) {
    fun initData(boardId: Long): PostPreviewList =
        recentPostListCollector.get(boardId).fillData()

    fun getsByBoardId(boardId: Long, page: Int, size: Int): PostPreviewList =
        postListCollector.get(PostListQuery(boardId = boardId, page = page, size = size)).fillData()

    private fun PostList.fillData(): PostPreviewList {
        val userMap = userCollector.multiGet(content.map { it.userId })
        val tagMap = postTagCollector.multiGet(content.map { it.id })
        val likeMap = postLikesCollector.multiGet(content.map { it.id })

        return PostPreviewList(
            content = content.map {
                PostPreview(
                    id = it.id,
                    boardId = it.boardId,
                    user = userMap[it.userId]!!,
                    title = it.title,
                    content = it.content,
                    isImageIncluded = it.isImageIncluded,
                    status = it.status,
                    tags = tagMap[it.id]?.content ?: emptyList(),
                    likeCount = likeMap[it.id]?.userIds?.size ?: 0,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            },
            hasNext = hasNext
        )
    }
}
