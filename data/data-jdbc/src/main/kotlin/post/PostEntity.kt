package waffle.guam.community.data.jdbc.post

import waffle.guam.community.data.jdbc.BaseTimeEntity
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.common.ImagePathsConverter
import waffle.guam.community.data.jdbc.like.PostLikeEntity
import waffle.guam.community.data.jdbc.scrap.PostScrapEntity
import waffle.guam.community.data.jdbc.tag.PostTagEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import javax.persistence.CascadeType
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Table(name = "posts")
@Entity
class PostEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    var boardId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: UserEntity,

    var title: String,

    var content: String,

    @Convert(converter = ImagePathsConverter::class)
    var images: List<String> = emptyList(),

    @Enumerated(value = EnumType.STRING)
    var status: Status = Status.VALID,

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tags: MutableSet<PostTagEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val comments: MutableList<PostCommentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val likes: MutableSet<PostLikeEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val scraps: MutableSet<PostScrapEntity> = mutableSetOf(),
) : BaseTimeEntity() {
    val isAnonymous: Boolean
        get() = boardId == 1L

    enum class Status {
        VALID, DELETED
    }
}
