package waffle.guam.community.data.jdbc.stack

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "stacks")
@Entity
class StackEntity private constructor(
    @EmbeddedId
    val data: StackId,
) {
    constructor(userId: Long, name: String) : this(StackId(userId, name))
}

val StackEntity.name
    get() = this.data.name

@Embeddable
class StackId(
    val userId: Long,

    @Column(length = 10)
    val name: String,
) : Serializable
