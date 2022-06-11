package waffle.guam.community.data.jdbc.comment

import waffle.guam.community.data.jdbc.BaseTimeEntity
import waffle.guam.community.data.jdbc.common.ImagePathsConverter
import waffle.guam.community.data.jdbc.post.PostEntity
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "post_comments")
@Entity
data class PostCommentEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val post: PostEntity,

    val userId: Long,

    var content: String,

    @Convert(converter = ImagePathsConverter::class)
    val images: MutableList<String> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    var status: Status = Status.VALID,
) : BaseTimeEntity() {

    private var mentionedUserIds: String = ""

    val mentionIds: List<Long>
        get() = if (mentionedUserIds.isEmpty()) {
            emptyList()
        } else {
            mentionedUserIds.split(",").map { id -> id.toLong() }
        }

    fun setMentionedUserIds(ids: List<Long>) {
        mentionedUserIds = ids.joinToString(",")
    }

    enum class Status {
        VALID, DELETED
    }
}
