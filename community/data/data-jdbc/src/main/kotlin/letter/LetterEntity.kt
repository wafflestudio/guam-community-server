package waffle.guam.community.data.jdbc.letter

import waffle.guam.community.data.jdbc.BaseTimeEntity
import waffle.guam.community.data.jdbc.common.ImagePathConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "letters")
@Entity
class LetterEntity(
    val senderId: Long,
    val receiverId: Long,

    @Column(length = 300)
    var text: String,

    @Convert(converter = ImagePathConverter::class)
    var image: String? = null,
) : BaseTimeEntity() {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L

    val pairId: String = arrayOf(senderId, receiverId).sorted().toString()

    var isRead: Boolean = false

    var status: Status = Status.ACTIVE

    enum class Status {
        ACTIVE, DELETED
    }
}

internal fun getPairIdOf(userOne: Long, userTwo: Long) =
    arrayOf(userOne, userTwo).sorted().toString()
