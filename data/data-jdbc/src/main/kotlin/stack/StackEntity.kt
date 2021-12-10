package waffle.guam.community.data.jdbc.stack

import waffle.guam.community.data.jdbc.user.UserEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "stacks")
@Entity
class StackEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: UserEntity,

    @Column(length = 10)
    val name: String,
)
