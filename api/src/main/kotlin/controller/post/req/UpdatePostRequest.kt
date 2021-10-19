package waffle.guam.community.controller.post.req

data class UpdatePostRequest(
    val title: String? = null,
    val content: String? = null,
    val tagId: Long? = null
)
