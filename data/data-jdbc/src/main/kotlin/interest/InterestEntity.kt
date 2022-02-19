package waffle.guam.community.data.jdbc.interest

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "interests")
@Entity
class InterestEntity private constructor(
    @EmbeddedId
    val data: InterestId,
) {
    constructor(userId: Long, name: String) : this(InterestId(userId, name))
}

val InterestEntity.name
    get() = this.data.name

@Embeddable
data class InterestId(
    val userId: Long,

    @Column(length = 10)
    val name: String,
) : Serializable
