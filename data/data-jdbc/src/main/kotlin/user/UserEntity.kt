package waffle.guam.community.data.jdbc.user

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "users")
@Entity
class UserEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long,
    val username: String,
)
