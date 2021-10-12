package waffle.guam.community.service.model

import waffle.guam.community.data.jdbc.comment.CommentEntity

data class Comment(
    val id: Long,
    val content: String
) {
    companion object {
        fun CommentEntity.toDomain() = Comment(id = id, content = content)
    }
}
