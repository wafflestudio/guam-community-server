package waffle.guam.community.data.jdbc.letter

import waffle.guam.community.data.jdbc.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.Version
import kotlin.math.max
import kotlin.math.min

@Table(
    name = "letter_boxes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["low_user_id", "high_user_id"])],
)
@Entity
class LetterBoxEntity(
    senderId: Long,
    receiverId: Long,
) : BaseTimeEntity() {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L

    @Column(name = "low_user_id")
    val lowUserId: Long = min(senderId, receiverId)
    var lowLastReadLetterId: Long = 0L
    var lowUserDeleted: Boolean = false
    var lowBlockedHigh: Boolean = false

    @Column(name = "high_user_id")
    val highUserId: Long = max(senderId, receiverId)
    var highLastReadLetterId: Long = 0L
    var highUserDeleted: Boolean = false
    var highBlockedLow: Boolean = false

    @Version
    val version: Long = 0L

    var isReported: Boolean = false

    fun lastReadLetterIdOf(userId: Long): Long =
        if (isLowUser(userId)) lowLastReadLetterId
        else highLastReadLetterId

    fun setLastReadLetterIdOf(userId: Long, value: Long) {
        if (isLowUser(userId)) lowLastReadLetterId = value
        else highLastReadLetterId = value
    }

    fun delete(userId: Long) {
        if (isLowUser(userId)) lowUserDeleted = true
        else highUserDeleted = true
    }

    fun blockOther(userId: Long) {
        delete(userId)
        if (isLowUser(userId)) {
            lowBlockedHigh = true
        } else {
            highBlockedLow = true
        }
    }

    fun report(userId: Long) {
        isReported = true
        blockOther(userId)
    }

    fun hasBlockedOther(userId: Long): Boolean =
        if (isLowUser(userId)) lowBlockedHigh
        else highBlockedLow

    fun isBlockedByOther(userId: Long): Boolean =
        if (isLowUser(userId)) highBlockedLow
        else lowBlockedHigh

    fun isDeletedBy(userId: Long): Boolean =
        if (isLowUser(userId)) lowUserDeleted
        else highUserDeleted

    fun visibleTo(userId: Long): Boolean =
        (!isReported && !isDeletedBy(userId) && !hasBlockedOther(userId))

    internal fun isLowUser(userId: Long): Boolean =
        if (userId !in userIdSet) {
            throw IllegalArgumentException("해당 쪽지함에 속한 참여자가 아닙니다.")
        } else {
            userId == lowUserId
        }

    internal val userIdSet: Set<Long>
        get() = setOf(lowUserId, highUserId)
}
