package waffle.guam.favorite.client.model

data class PostInfo(
    val postId: Long,
    val likeCnt: Int,
    val scrapCnt: Int,
    val like: Boolean,
    val scrap: Boolean,
)
