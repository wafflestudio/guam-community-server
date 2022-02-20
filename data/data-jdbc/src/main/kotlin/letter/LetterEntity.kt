package waffle.guam.community.data.jdbc.letter

import waffle.guam.community.data.jdbc.BaseTimeEntity
import waffle.guam.community.data.jdbc.common.ImagePathConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "letters")
@Entity
class LetterEntity(
    val userId: Long,
    val sentBy: Long,
    val sentTo: Long,

    @Column(length = 300)
    val text: String,

    @Convert(converter = ImagePathConverter::class)
    var imagePath: String? = null,
) : BaseTimeEntity() {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L

    @Enumerated(EnumType.STRING)
    var status: Status = Status.ACTIVE

    var isRead: Boolean = sentBy == userId

    fun read() {
        isRead = true
    }

    enum class Status {
        ACTIVE, DELETED
    }
}
