package waffle.guam.community.data.r2dbc.comment

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("post_comments")
data class PostCommentEntity(
    @Column("user_id")
    val userId: Long,

    var content: String,

    var status: Status,

    @Column("post_id")
    val postId: Long,

    @Id
    var id: Long = 0L,
) {

    private var mentionedUserIds: String = ""

    val mentionIds: List<Long>
        get() = if (mentionedUserIds.isEmpty()) {
            emptyList()
        } else {
            mentionedUserIds.split(",").map { id -> id.toLong() }
        }

    enum class Status {
        VALID, DELETED
    }
}

data class PostCommentDto
@PersistenceConstructor constructor(
    val id: Long = 0L,
    val userId: Long,
    var content: String,
    var status: PostCommentEntity.Status,
    val postId: Long,
    val postBoardId: Long,
) {
    val isAnonymous: Boolean
        get() = postBoardId == 1L
}
