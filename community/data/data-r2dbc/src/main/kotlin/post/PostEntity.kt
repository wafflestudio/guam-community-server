package waffle.guam.community.data.r2dbc.post

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * TODO Relations
 */
@Table("posts")
data class PostEntity(
    @Id
    var id: Long = 0L,
    var boardId: Long,
    val userId: Long,
    var title: String,
    var content: String,
    var status: Status,
) {
    val isAnonymous: Boolean
        get() = boardId == 1L

    enum class Status {
        VALID, DELETED
    }
}
