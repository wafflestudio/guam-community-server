package waffle.guam.community.data.jdbc.post

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import waffle.guam.community.data.jdbc.BaseTimeEntity
import waffle.guam.community.data.jdbc.comment.PostCommentEntity
import waffle.guam.community.data.jdbc.like.PostLikeEntity
import waffle.guam.community.data.jdbc.tag.PostTagEntity
import javax.persistence.AttributeConverter
import javax.persistence.CascadeType
import javax.persistence.Convert
import javax.persistence.Converter
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@EntityListeners(AuditingEntityListener::class)
@Table(name = "posts")
@Entity
class PostEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    val boardId: Long,

    val userId: Long,

    val title: String,

    val content: String,

    @Convert(converter = ImagePathsConverter::class)
    var images: List<String>,

    @Enumerated(value = EnumType.STRING)
    var status: Status = Status.VALID,

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tags: MutableSet<PostTagEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val comments: MutableList<PostCommentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val likes: MutableSet<PostLikeEntity> = mutableSetOf(),
) : BaseTimeEntity() {

    enum class Status {
        VALID, DELETED
    }

    @Converter
    private class ImagePathsConverter : AttributeConverter<List<String>, String> {

        override fun convertToDatabaseColumn(submitContents: List<String>?): String? =
            if (submitContents.isNullOrEmpty()) {
                null
            } else {
                submitContents.joinToString(",")
            }

        override fun convertToEntityAttribute(dbData: String?): List<String> =
            dbData?.split(",") ?: emptyList()
    }
}
