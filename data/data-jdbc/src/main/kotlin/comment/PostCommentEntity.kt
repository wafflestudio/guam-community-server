package waffle.guam.community.data.jdbc.comment

import waffle.guam.community.data.jdbc.BaseTimeEntity
import waffle.guam.community.data.jdbc.common.ImagePathsConverter
import waffle.guam.community.data.jdbc.like.PostCommentLikeEntity
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.user.UserEntity
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
import javax.persistence.OneToMany
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

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val user: UserEntity,

    var content: String,

    @Convert(converter = ImagePathsConverter::class)
    val images: MutableList<String> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment")
    val likes: MutableList<PostCommentLikeEntity> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    var status: Status = Status.VALID,
) : BaseTimeEntity() {

    enum class Status {
        VALID, DELETED
    }
}
