package waffle.guam.community.data.jdbc.post

import waffle.guam.community.data.jdbc.BaseTimeEntity
import waffle.guam.community.data.jdbc.category.PostCategoryEntity
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.common.ImagePathsConverter
import javax.persistence.CascadeType
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * @property categories 현재는 post 당 하나만 사용하지만, 확장 가능성을 고려해 M:N 형태 유지
 */
@Table(name = "posts")
@Entity
class PostEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    var boardId: Long,

    val userId: Long,

    var title: String,

    var content: String,

    @Convert(converter = ImagePathsConverter::class)
    var images: List<String> = emptyList(),

    @Enumerated(value = EnumType.STRING)
    var status: Status = Status.VALID,

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val categories: MutableSet<PostCategoryEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val comments: MutableList<PostCommentEntity> = mutableListOf(),
) : BaseTimeEntity() {
    val isAnonymous: Boolean
        get() = boardId == 1L

    enum class Status {
        VALID, DELETED
    }
}
