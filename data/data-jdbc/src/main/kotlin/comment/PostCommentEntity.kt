package waffle.guam.community.data.jdbc.comment

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import java.time.Instant
import javax.persistence.AttributeConverter
import javax.persistence.Convert
import javax.persistence.Converter
import javax.persistence.Entity
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
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val user: UserEntity,
    val content: String,
    @Convert(converter = ImagePathsConverter::class)
    val images: List<String> = mutableListOf(),
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = createdAt
) {
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
